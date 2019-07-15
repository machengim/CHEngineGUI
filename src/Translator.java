import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

public class Translator
{
    private HashMap<String, String> varTable;
    private HashMap<String, HashMap<String, String>> arrTable;
    private int index_limit, recents_limit;
    private LinkedList<Post> posts;

    public Translator(Site site)
    {
        varTable = new HashMap<>();
        varTable.put("site_name", site.getSiteName());
        varTable.put("site_url", site.getSiteUrl());
        varTable.put("site_path", site.getSitePath());
        String themePath = "themes" + File.separator + site.getTheme().getThemeName();
        varTable.put("theme_path", themePath);
        arrTable = new HashMap<>();
        arrTable.put("navigations", site.getTheme().getNavigations());
        arrTable.put("links", site.getTheme().getLinks());
        arrTable.put("contacts", site.getTheme().getContacts());
        index_limit = site.getTheme().getPostsOnIndex();
        recents_limit = site.getTheme().getRecentPostSize();
        posts = site.getPosts();
    }

    public String tranlate(String text, int op)
    {
        StringBuilder sb = new StringBuilder();
        int i = -1, j = 0;
        while ((i = text.indexOf("[$", i)) >= 0)
        {
            sb.append(text.substring(j, i));
            j = text.indexOf("$]", i);
            if (text.substring(i, j).contains("for"))
            {
                j = text.indexOf("[$endfor$]", j);
                sb.append(handleLoop(text.substring(i, j + 10), op));
                j = i = j + 10;
            }
            else
            {
                sb.append(getVarible(text.substring(i, j + 2), op));
                j = i = j + 2;
            }
        }
        sb.append(text.substring(j));
        return sb.toString();
    }

    private String getVarible(String text, int op)
    {
        int i = text.indexOf('$');
        int j = text.indexOf('$', i + 1);
        String varName = text.substring(i + 1, j);
        return getRealUrl(varTable.get(varName), op);
    }

    private String handleLoop(String text, int op)
    {
        StringBuilder sb = new StringBuilder();
        int i = text.indexOf(':');
        int j = text.indexOf('$', i + 1);
        String arrName = text.substring(i + 1, j);
        i = j + 2;
        j = text.lastIndexOf("[$endfor$]");
        String trimedText = text.substring(i, j);
        if (arrName.equals("posts") || arrName.equals("recents"))
            replacePostLoop(trimedText, arrName, op);   //note that loop is in the child function.
        else
            arrTable.get(arrName).forEach((key, value) -> sb.append(replaceLoop(key, value, trimedText, op)));
        return sb.toString();
    }

    private String replaceLoop(String key, String value, String text, int op)
    {
        StringBuilder sb = new StringBuilder(text);
        int i, j;
        while ((i = sb.indexOf("[$")) >= 0)
        {
            j = sb.indexOf("$]", i);
            String varName = sb.substring(i, j + 2);
            if (varName.contains("key"))
                sb.replace(i, j + 2, key);
            else if (varName.contains("value"))
                sb.replace(i, j + 2, getRealUrl(value, op));
            else
                sb.replace(i, j + 2, getVarible(varName, op));
        }
        return sb.toString();
    }

    private String replacePostLoop(String text, String option, int op)
    {
        StringBuilder sb = new StringBuilder();
        int limit;
        if (option.equals("posts"))
            limit = index_limit;
        else
            limit = recents_limit;
        for (int t = 0; t < posts.size() && t < limit; t++)
        {
            StringBuilder temp = new StringBuilder(text);
            Post post = posts.get(t);
            int i, j;
            while ((i = temp.indexOf("[$")) >= 0)
            {
                j = temp.indexOf("$]", i);
                String varName = temp.substring(i, j + 2);
                if (varName.contains("post_url"))       // note the path
                    temp.replace(i, j + 2, getRealUrl(post.getUrl(), op));
                else if (varName.contains("post_title"))
                    temp.replace(i, j + 2, post.getTitle());
                else if (varName.contains("post_date"))
                    temp.replace(i, j + 2, post.getDate());
                else if (varName.contains("post_author"))
                    temp.replace(i, j + 2, post.getAuthor());
                else if (varName.contains("post_brief"))
                    temp.replace(i, j + 2, post.getBrief());
                else if (varName.contains("post_content"))
                    temp.replace(i, j + 2, post.getContent());
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    private String getRealUrl(String url, int op)
    {
        if (url == null)
            return "";
        if (op == 1 || url.contains("http") || url.contains(":/")  || url.startsWith("www"))
            return url;
        else if (url.contains(".") || url.contains("themes"))
            return "../" + url;
        return url;
    }

}