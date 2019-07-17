import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

public class Site implements Serializable {
    private String siteName;
    private String siteUrl;
    private String sitePath;
    private int nextID;
    private int nextPageID;
    private Theme theme;
    private LinkedList<Post> posts;
    private LinkedList<Page> pages;
    private LinkedList<String> cats;

    public static final long serialVersionUID = 9527L;
    public static final String DRAFT_PATH = "draft";
    public static final String POST_PATH = "posts";
    public static final String PAGE_PATH = "pages";
    public static final String ARCHIVE_PATH = "archive";
    public static final String DATA_FILE = "save.ser";


    public Site(){}

    public Site(String siteName, String siteUrl, String sitePath) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.sitePath = sitePath;
        this.nextID = 1;
        this.nextPageID = 1;
        posts = new LinkedList<>();
        pages = new LinkedList<>();
        cats = new LinkedList<>();
        initializeDirectories();
    }

    public Site(String siteName, String siteUrl, String sitePath, Theme theme) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.sitePath = sitePath;
        this.theme = theme;
        this.nextID = 0;
        posts = new LinkedList<>();
        pages = new LinkedList<>();
        cats = new LinkedList<>();
        initializeDirectories();
    }


    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSitePath() {
        return sitePath;
    }

    public void setSitePath(String sitePath) {
        this.sitePath = sitePath;
    }

    public int getNextID() {
        return nextID;
    }

    public void setNextID(int nextID) {
        this.nextID = nextID;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public LinkedList<Post> getPosts() {
        return posts;
    }

    public void setPosts(LinkedList<Post> posts) {
        this.posts = posts;
    }

    public LinkedList<String> getCats() {
        return cats;
    }

    public void setCats(LinkedList<String> cats) {
        this.cats = cats;
    }



    public String generateDraft(int type)
    {
        String draftPath = sitePath + File.separator + DRAFT_PATH;
        if (!FileIO.isDir(draftPath))
            FileIO.mkDir(draftPath);
        String newDraftContent = "---\n!Important: DO NOT modify the ID number! Lines with * are mandatory.";
        String draftFile;
        if (type == 0)
        {   newDraftContent = newDraftContent + "Use ',' to split tags.\n*id: " + nextID
                    + "\ntitle: \nauthor: Cheng\ncat: \ntags: \n*date: " + Util.getToday()
                    + "\n*url: " + nextID + ".html\n---\n\n";
            draftFile = draftPath + File.separator + nextID + ".md";
            nextID++;
        }
        else {
            newDraftContent = newDraftContent + "\n*id:" + nextPageID + "\ntitle: \ndate: " + Util.getToday()
                    + "\n*url: p" + nextPageID + ".html\n*type: page\n---\n\n";
            draftFile = draftPath + File.separator + "p" + nextPageID + ".md";
            nextPageID++;
        }
        FileIO.writeFile(draftFile, newDraftContent);
        return draftFile;
    }

    public void generateWholeSite()
    {
        posts.clear();
        cats.clear();
        String jsonFile = getSitePath() + File.separator+ "themes" + File.separator + theme.getThemeName() + File.separator + "config.json";
        theme.readFromJson(jsonFile);
        String draftPath = sitePath + File.separator + DRAFT_PATH;
        String[] draftList = FileIO.getFiles(draftPath);
        for (String draftFile: draftList)
        {
            if (!draftFile.contains(".md"))
                continue;
            LinkedList<StringBuilder> mdLines = FileIO.readMdFile(draftPath + File.separator + draftFile);
            String[] metas = Convert.readMeta(mdLines);
            if (metas[7].contains("y"))
                getPage(mdLines, metas);
            else
                getPost(mdLines, metas);
        }
        Collections.sort(posts, Collections.reverseOrder());
        String postPath = sitePath + File.separator + POST_PATH;
        if (!FileIO.isDir(postPath))
            FileIO.mkDir(postPath);
        String archivePath = sitePath + File.separator + ARCHIVE_PATH;
        if (!FileIO.isDir(archivePath))
            FileIO.mkDir(archivePath);
        String pagePath = sitePath + File.separator + PAGE_PATH;
        if (!FileIO.isDir(pagePath))
            FileIO.mkDir(pagePath);
        FileIO.emptyDir(postPath);
        FileIO.emptyDir(archivePath);
        FileIO.emptyDir(pagePath);
        Translator translator = new Translator(this);
        String outputFile;
        for (Post post:posts)
        {
            translator.setPostInfo(post);
            outputFile = sitePath + File.separator +  post.getUrl();
            generatHtml(translator, "generic", outputFile, null);
            post.setMtime(Util.getNow());
        }
        for (Page page: pages)
        {
            translator.setPageInfo(page);
            outputFile = sitePath + File.separator + page.getUrl();
            generatHtml(translator, "generic", outputFile, null);
            page.setMtime(Util.getNow());
        }
        generateCommonFiles();
        saveSite();
    }

    public void checkPostModification()
    {
        String[] draftList = FileIO.getFiles(sitePath + File.separator + DRAFT_PATH);
        TreeMap<Integer, String> draftMap = new TreeMap<>();
        for (String draft: draftList)
        {
            if (!draft.contains(".md"))
                continue;
            String draftFile = sitePath + File.separator + DRAFT_PATH + File.separator + draft;
            LinkedList<StringBuilder> mdLines = FileIO.readMdFile(draftFile);
            String[] metas = Convert.readMeta(mdLines);
            if (metas[7].contains("y"))
                continue;
            int draftID = Util.StrToInt(metas[0]);
            draftMap.put(draftID, draftFile);
        }
        TreeMap<Integer, Post> postMap = new TreeMap<>();
        for (Post post: posts)
            postMap.put(post.getId(), post);
        int flag = 0;
        while (draftMap.size() > 0 && postMap.size() > 0)
        {
            int i = draftMap.firstKey(), j = postMap.firstKey();
            if (i > j)
            {
                posts.remove(postMap.get(j));
                postMap.remove(j);
                flag = 1;
                continue;
            }
            else if (i < j)
            {
                newPost(draftMap.get(i));
                draftMap.remove(i);
                flag = 1;
                continue;
            }
            else if (FileIO.getMtime(draftMap.get(i)).compareTo(postMap.get(j).getMtime()) > 0)
            {
                posts.remove(postMap.get(j));
                flag = 1;
                newPost(draftMap.get(i));
            }
            postMap.remove(j);
            draftMap.remove(i);
        }
        while (draftMap.size() > 0)
        {
            int i = draftMap.firstKey();
            newPost(draftMap.get(i));
            draftMap.remove(i);
            flag = 1;
        }
        while (postMap.size() > 0)
        {
            int i = postMap.firstKey();
            posts.remove(postMap.get(i));
            postMap.remove(i);
            flag = 1;
        }

        if (flag == 1)      //signal some changes taken place.
        {
            generateCommonFiles();
            saveSite();
        }
    }

    public void generateCommonFiles()
    {
        Collections.sort(posts, Collections.reverseOrder());
        String archivePath = sitePath + File.separator + ARCHIVE_PATH;
        Translator translator = new Translator(this);
        String outputFile = archivePath + File.separator + "index.html";
        generatHtml(translator, "archive", outputFile, null);
        for (String cat: cats)
        {
            outputFile = archivePath + File.separator + cat + ".html";
            generatHtml(translator, "archive", outputFile, cat);
        }
        outputFile = sitePath + File.separator + "index.html";
        generatHtml(translator, "index", outputFile, null);
    }

    private void initializeDirectories()
    {
        String draftPath = sitePath + File.separator + DRAFT_PATH;
        String postPath = sitePath + File.separator + POST_PATH;
        String archivePath = sitePath + File.separator + ARCHIVE_PATH;
        String pagePath = sitePath + File.separator + PAGE_PATH;
        String[] paths = {draftPath, postPath, archivePath, pagePath};
        for (String path: paths) {
            if (FileIO.isDir(path))
                FileIO.clearDir(path);
            else
                FileIO.mkDir(path);
        }
    }

    private void newPost(String draftFile)
    {
        LinkedList<StringBuilder> mdLines = FileIO.readMdFile(draftFile);
        String[] metas = Convert.readMeta(mdLines);
        Post post = getPost(mdLines, metas);
        Translator ts = new Translator(this);
        ts.setPostInfo(post);
        String outputFile = sitePath + File.separator + post.getUrl();
        generatHtml(ts, "generic", outputFile, null);
    }

    private void generatHtml(Translator ts, String template, String outputFile, String cat) //option cat is only used for category's archive page.
    {
        int op = (template.equals("index")) ? 1 : 0;
        if (cat != null)
            op = 2;
        String templateFile = sitePath + File.separator + "themes" + File.separator + theme.getThemeName() + File.separator + template + ".tpl";
        String templateText = FileIO.readFile(templateFile);
        String outputText = ts.tranlate(templateText, op, cat);
        FileIO.writeFile(outputFile, outputText);
    }

    private Post getPost(LinkedList<StringBuilder> mdLines, String[] metas)
    {
        //meta[]: 0 id, 1 title, 2 cat, 3 tags, 4 date, 5 url, 6, author, 7, is page.
        int postID = Util.StrToInt(metas[0]);
        if (postID == -1)
            return null;
        else if (postID >= nextID)
            nextID = postID + 1;
        String[] tags = null;
        if (metas[3] != null && metas[3].length() != 0)
            tags = metas[3].split(",");
        String mtime = Util.getNow();
        Post post = new Post(postID, metas[1], metas[2], tags, metas[4], POST_PATH + File.separator + metas[5], mtime, metas[6]);
        posts.add(post);
        if (metas[2] != null && metas[2].length() != 0 && !cats.contains(metas[2]))
            cats.add(metas[2]);
        Convert.dumpMeta(mdLines);
        String postContent = Convert.toHtml(mdLines);
        String postBrief = Convert.getBrief(postContent, 256);
        post.setBrief(postBrief);
        post.setContent(postContent);
        return post;
    }

    private void getPage(LinkedList<StringBuilder> mdLines, String[] metas)
    {
        int pageID = Util.StrToInt(metas[0]);
        if (pageID == -1)
            return;
        else if (pageID >= nextPageID)
            nextPageID = pageID + 1;
        Page page = new Page(pageID, metas[1], metas[4], PAGE_PATH + File.separator + metas[5], Util.getNow());
        pages.add(page);
        System.out.println("page add ok!");
        Convert.dumpMeta(mdLines);
        String pageContent = Convert.toHtml(mdLines);
        page.setContent(pageContent);
    }

    public void saveSite()
    {
        String dataFile = sitePath + File.separator + DATA_FILE;
        try(FileOutputStream fos = new FileOutputStream(dataFile))
        {
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Created data file successfully!");
    }

    public static Site loadSite(String dataFile)
    {
        Site s = null;
        try(FileInputStream fis = new FileInputStream(dataFile))
        {
            ObjectInputStream in = new ObjectInputStream(fis);
            s = (Site) in.readObject();
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        System.out.println("Read site data successfully!");
        return s;
    }

}
