import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Theme  implements Serializable {

    public static final long serialVersionUID = 9527L;

    private String themeName;
    private int postsOnIndex;
    private int briefSize;
    private int recentPostSize;
    private LinkedHashMap<String, String> navigations;
    private LinkedHashMap<String, String> contacts;
    private LinkedHashMap<String, String> links;

    public Theme(){}

    public Theme(String themeName)
    {
        this.themeName = themeName;
        navigations = new LinkedHashMap<>();
        contacts = new LinkedHashMap<>();
        links = new LinkedHashMap<>();
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getPostsOnIndex() {
        return postsOnIndex;
    }

    public void setPostsOnIndex(int postsOnIndex) {
        this.postsOnIndex = postsOnIndex;
    }

    public int getBriefSize() {
        return briefSize;
    }

    public void setBriefSize(int briefSize) {
        this.briefSize = briefSize;
    }

    public int getRecentPostSize() {
        return recentPostSize;
    }

    public void setRecentPostSize(int recentPostSize) {
        this.recentPostSize = recentPostSize;
    }

    public LinkedHashMap<String, String> getNavigations() {
        return navigations;
    }

    public void setNavigations(LinkedHashMap<String, String> navigations) {
        this.navigations = navigations;
    }

    public LinkedHashMap<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(LinkedHashMap<String, String> contacts) {
        this.contacts = contacts;
    }

    public LinkedHashMap<String, String> getLinks() {
        return links;
    }

    public void setLinks(LinkedHashMap<String, String> links) {
        this.links = links;
    }

    public void readFromJson(String jsonFile)
    {
        navigations.clear();
        contacts.clear();
        links.clear();
        System.out.println("now reading from " + jsonFile);
        try{
            String config = FileIO.readFile(jsonFile);
            JSONObject jo = new JSONObject(config);
            postsOnIndex = jo.getInt("posts_on_index");
            briefSize = jo.getInt("brief_size");
            recentPostSize = jo.getInt("recent_posts_size");
            JSONArray ja = jo.getJSONArray("navigations");
            readMapFromJson(navigations, ja);
            ja = jo.getJSONArray("contacts");
            readMapFromJson(contacts, ja);
            ja = jo.getJSONArray("links");
            readMapFromJson(links, ja);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void readMapFromJson(LinkedHashMap<String, String> map, JSONArray ja)
    {
        if (ja == null || ja.length() == 0)
            return;
        try {
             for (int i = 0; i < ja.length(); i++) {
                 String name = ja.getJSONObject(i).getString("name");
                 String url = ja.getJSONObject(i).getString("url");
                 map.put(name, url);
             }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
