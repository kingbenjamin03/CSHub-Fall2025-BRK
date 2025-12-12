package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.ebean.Finder;
import io.ebean.Model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = InterviewNotification.class)
@ToString
public class InterviewNotification extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "interview_id", referencedColumnName = "id")
    private RAInterview interview;

    @Column(name = "notification_type")
    private String notificationType; // scheduled, reminder, rescheduled, canceled

    @ManyToOne
    @JoinColumn(name = "sent_to", referencedColumnName = "id")
    private User sentTo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "sent_time")
    private Date sentTime;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    /****************** Constructors **********************************************************************************/

    public InterviewNotification() {
    }

    public InterviewNotification(long id) {
        this.id = id;
    }

    /****************** End of Constructors ***************************************************************************/

    public static Finder<Long, InterviewNotification> find =
            new Finder<Long, InterviewNotification>(InterviewNotification.class);

    /****************** Utility functions *****************************************************************************/

    @PrePersist
    void createdAt() {
        if (sentTime == null) {
            sentTime = new Date();
        }
    }

    /****************** End of Utility functions **********************************************************************/
}

