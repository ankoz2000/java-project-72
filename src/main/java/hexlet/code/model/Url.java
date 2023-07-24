package hexlet.code.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Url {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private Date createdAt;
}
