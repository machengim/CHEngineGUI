import java.io.Serializable;

public class Post implements Serializable, Comparable<Post> {

    public static final long serialVersionUID = 9527L;
    private int id;
    private String title;
    private String author;
    private String cat;
    private String[] tags;
    private String date;
    private String url;
    private String mtime;
    private String brief;
    private transient String content;

    public Post(){}


    public Post(int id, String title, String cat, String[] tags, String date, String url, String mtime, String author) {
        this.id = id;
        this.title = (title == null || title.length() == 0)? "No title" : title;
        this.author = (author == null || author.length() == 0)? "Anonymous": author;
        this.cat = (cat == null || cat.length() == 0)? "" : cat;
        this.date = date;
        this.mtime = mtime;
        this.url = url;
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(Post o) {
        return this.date.compareTo(o.date);
    }
}
