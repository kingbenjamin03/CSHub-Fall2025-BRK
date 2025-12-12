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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = FacultyAvailability.class)
@ToString
public class FacultyAvailability extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "faculty_id", referencedColumnName = "id")
    private User faculty;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_time")
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_time")
    private Date updatedTime;

    /****************** Constructors **********************************************************************************/

    public FacultyAvailability() {
    }

    public FacultyAvailability(long id) {
        this.id = id;
    }

    /****************** End of Constructors ***************************************************************************/

    public static Finder<Long, FacultyAvailability> find =
            new Finder<Long, FacultyAvailability>(FacultyAvailability.class);

    /****************** Utility functions *****************************************************************************/

    @PrePersist
    void createdAt() {
        if (createdTime == null) {
            createdTime = new Date();
        }
        updatedTime = new Date();
    }

    @PreUpdate
    void updatedAt() {
        updatedTime = new Date();
    }

    /****************** End of Utility functions **********************************************************************/
}

