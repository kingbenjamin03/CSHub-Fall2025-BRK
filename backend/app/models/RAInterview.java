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
@Table(name = "ra_interview")
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = RAInterview.class)
@ToString
public class RAInterview extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "rajob_application_id", referencedColumnName = "id")
    private RAJobApplication raJobApplication;

    @ManyToOne
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private User faculty;

    @ManyToOne
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    private User applicant;

    @ManyToOne
    @JoinColumn(name = "rajob_id", referencedColumnName = "id")
    private RAJob raJob;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "interview_date")
    private Date interviewDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "interview_time")
    private Date interviewTime;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status; // pending, confirmed, rescheduled, canceled, declined

    @Column(name = "student_response")
    private String studentResponse; // null, accepted, declined, reschedule_requested

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_time")
    private Date updatedTime;

    /****************** Constructors **********************************************************************************/

    public RAInterview() {
    }

    public RAInterview(long id) {
        this.id = id;
    }

    /****************** End of Constructors ***************************************************************************/

    public static Finder<Long, RAInterview> find =
            new Finder<Long, RAInterview>(RAInterview.class);

    /****************** Utility functions *****************************************************************************/

    @PrePersist
    void createdAt() {
        if (createdTime == null) {
            createdTime = new Date();
        }
        updatedTime = new Date();
        if (status == null) {
            status = "pending";
        }
    }

    @PreUpdate
    void updatedAt() {
        updatedTime = new Date();
    }

    /****************** End of Utility functions **********************************************************************/
}

