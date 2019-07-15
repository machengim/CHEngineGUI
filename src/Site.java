import java.io.*;
import java.util.LinkedList;

public class Site implements Serializable {
    //need theme, posts
    private String siteName;
    private String siteUrl;
    private String sitePath;
    private int nextID;
    private Theme theme;
    private LinkedList<Post> posts;
    private LinkedList<String> cats;

    public static final long serialVersionUID = 9527L;
    public static final String DRAFT_PATH = "draft";
    public static final String POST_PATH = "posts";
    public static final String ARCHIVE_PATH = "archive";
    public static final String DATA_FILE = "save.ser";


    public Site(){}

    public Site(String siteName, String siteUrl, String sitePath) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.sitePath = sitePath;
        this.nextID = 1;
        posts = new LinkedList<>();
        cats = new LinkedList<>();
    }

    public Site(String siteName, String siteUrl, String sitePath, Theme theme) {
        this.siteName = siteName;
        this.siteUrl = siteUrl;
        this.sitePath = sitePath;
        this.theme = theme;
        this.nextID = 0;
        posts = new LinkedList<>();
        cats = new LinkedList<>();
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



    public String generateDraft()
    {
        String newDraftContent = "---\n!! Important: DO NOT modify the ID number!! Lines with * are mandatory."
                + "Use ',' to split tags.\n*id: " + nextID + "\n*title: \ncat: \ntags: \n*date: "
                + Util.getToday() + "\n*url: " + Util.getToday() + ".html\n---\n\n";
        String draftPath = sitePath + File.separator + DRAFT_PATH;
        if (!FileIO.isDir(draftPath))
            FileIO.mkDir(draftPath);
        String draftFile = draftPath + File.separator + nextID + ".md";
        FileIO.writeFile(draftFile, newDraftContent);
        nextID++;
        return draftFile;
    }

    public void generateWholeSite()
    {
        String draftPath = sitePath + File.separator + DRAFT_PATH;
        String[] draftList = FileIO.getFiles(draftPath);
        for (String draftFile: draftList)
        {
            if (!draftFile.contains(".md"))
                continue;
           getPost(draftPath + File.separator + draftFile);
        }
        String postPath = sitePath + File.separator + POST_PATH;
        if (!FileIO.isDir(postPath))
            FileIO.mkDir(postPath);
        Translator translator = new Translator(this);
        for (Post post:posts)
        {
            String templateFile = sitePath + File.separator + "themes" + File.separator + theme.getThemeName() + File.separator + "generic.tpl";
            String templateText = FileIO.readFile(templateFile);
            String outputText = translator.tranlate(templateText, 0);
            String outputFile = sitePath + File.separator + POST_PATH + File.separator + post.getId() + ".html";
            FileIO.writeFile(outputFile, outputText);
        }
    }

    private void getPost(String draftFile)
    {
        LinkedList<StringBuilder> mdLines = FileIO.readMdFile(draftFile);
        String[] metas = Convert.readMeta(mdLines);         //meta[]: 0 id, 1 title, 2 cat, 3 tags, 4 date, 5 url.
        int postID = Util.StrToInt(metas[0]);
        if (postID == -1)
            return;
        String[] tags = null;
        if (metas[3] != null && metas[3].length() != 0)
            tags = metas[3].split(",");
        String mtime = Util.getNow();
        Post post = new Post(postID, metas[1], metas[2], tags, metas[4], metas[5], mtime);
        posts.add(post);
        if (!cats.contains(metas[2]))
            cats.add(metas[2]);
        Convert.dumpMeta(mdLines);
        String postContent = Convert.toHtml(mdLines);
        String postBrief = Convert.getBrief(postContent, 256);
        post.setBrief(postBrief);
        post.setContent(postContent);
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
