package models;

import io.ebean.Model;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import io.ebean.Finder;

@Entity
@Table(name = "user_message")
@Getter
@Setter
public class UserMessage extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long authorId;

    @Column(columnDefinition = "TEXT", length = 5000, nullable = false)
    private String content;

    private String createTime;
    private String updateTime;

    private String isActive = "True";

    public static final Finder<Long, UserMessage> find = new Finder<>(UserMessage.class);
}
