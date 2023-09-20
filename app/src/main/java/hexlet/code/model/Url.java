package hexlet.code.model;

import java.sql.Timestamp;

public final class Url {

    private Integer id;
    private String name;

    private Timestamp createdAt;


    public Url(String name) {
        this.name = name;
    }

    public Url() {
        name = "";
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
