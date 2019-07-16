import java.io.Serializable;

public class Page implements Serializable, Comparable<Page> {

    public static final long serialVersionUID = 9527L;

    private int id;
    private String title;
    private String date;
    private String url;
    private String mtime;
    private transient String content;

    public Page(int id, String title, String date, String url, String mtime) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.url = url;
        this.mtime = mtime;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(Page o) {
        return this.id - o.id;
    }
}
