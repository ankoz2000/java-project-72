package hexlet.code.model;

import java.sql.Timestamp;

public final class UrlCheck {

    private Integer id;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private Timestamp createdAt;
    private Integer urlId;

    public UrlCheck() {
    }

    public UrlCheck(String title) {
        this.title = title;
    }

    public UrlCheck(String title, Integer statusCode) {
        this.statusCode = statusCode;
        this.title = title;
    }

    public UrlCheck(Integer statusCode, String title, String h1, String description) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUrlId() {
        return urlId;
    }

    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }
}
