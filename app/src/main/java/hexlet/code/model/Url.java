package hexlet.code.model;

import java.sql.Timestamp;

public final class Url {

    private Long id;
    private String name;

    private Timestamp createdAt;

    private UrlCheck urlCheck;


    public Url(String name) {
        this.name = name;
    }

    public Url() {
        name = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public UrlCheck getUrlCheck() {
        return urlCheck;
    }

    public void setUrlCheck(UrlCheck urlCheck) {
        this.urlCheck = urlCheck;
    }
}
