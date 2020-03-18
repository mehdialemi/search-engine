package ir.co.realtime.websearcher.document;

import javax.persistence.Entity;

@Entity
public class WebDocument {

    private int id;
    private long time;
    private String url;
    private String domain;
    private String anchors;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAnchors() {
        return anchors;
    }

    public void setAnchors(String anchors) {
        this.anchors = anchors;
    }
}
