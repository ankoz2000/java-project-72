package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import java.time.Instant;

import hexlet.code.model.Url;

@Entity
public final class UrlCheck extends Model {

    @Id
    @GeneratedValue
    private Integer id;
    private Integer statusCode;
    private String title;
    private String h1;
    @Lob
    private String description;
    @ManyToOne
    private Url url;
    @WhenCreated
    private Instant createdAt;

    public UrlCheck(String title) {
        this.title = title;
    }

    public UrlCheck(Url url) {
        this.url = url;
    }

    public UrlCheck(String title, Integer statusCode, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.url = url;
    }

    public UrlCheck(Integer statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }
}
