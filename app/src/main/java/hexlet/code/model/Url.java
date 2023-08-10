package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public final class Url extends Model {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    @WhenCreated
    private Instant createdAt;

    @OneToMany(mappedBy="url")
    private List<UrlCheck> urlCheck;

    public Url(String name) {
        this.name = name;
    }

    public Url() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<UrlCheck> getUrlCheck() {
        return urlCheck;
    }

    public void setUrlCheck(List<UrlCheck> urlCheck) {
        this.urlCheck = urlCheck;
    }
}
