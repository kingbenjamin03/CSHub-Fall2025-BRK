package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import play.Logger;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = RAInterview.class)
public class RAInterview {
    private long id;
    private RAJobApplication raJobApplication;
    private User faculty;
    private User applicant;
    private RAJob raJob;
    private String interviewDate;
    private String interviewTime;
    private String meetingLink;
    private String location;
    private String status; // pending, confirmed, rescheduled, canceled, declined
    private String studentResponse; // null, accepted, declined, reschedule_requested
    private String notes;
    private String createdTime;
    private String updatedTime;

    /*********************************************** Constructors *****************************************************/
    public RAInterview() {
    }

    public RAInterview(long id) {
        this.id = id;
    }

    /*********************************************** Utility methods **************************************************/

    /**
     * Deserializes the json to a RAInterview.
     *
     * @param node the node to convert from.
     * @return the RAInterview object.
     */
    public static RAInterview deserialize(JsonNode node) throws Exception {
        try {
            if (node == null) {
                throw new NullPointerException("RAInterview node should not be empty for RAInterview.deserialize()");
            }
            if (node.get("id") == null) {
                return null;
            }

            RAInterview interview = Json.fromJson(node, RAInterview.class);
            return interview;

        } catch (Exception e) {
            Logger.debug("RAInterview.deserialize() exception: " + e.toString());
            throw new Exception("RAInterview.deserialize() exception: " + e.toString());
        }
    }

    /**
     * This utility method intends to return a list of RAInterview from JsonNode.
     *
     * @param jsonArray the json array to convert from.
     * @return a list of RAInterview objects.
     */
    public static List<RAInterview> deserializeJsonToList(JsonNode jsonArray, int startIndex, int endIndex)
            throws Exception {
        List<RAInterview> interviewList = new ArrayList<RAInterview>();
        for (int i = startIndex; i <= endIndex && i < jsonArray.size(); i++) {
            JsonNode json = jsonArray.path(i);
            RAInterview interview = RAInterview.deserialize(json);
            interviewList.add(interview);
        }
        return interviewList;
    }

    /*********************************************** Getters & Setters ************************************************/
    
    // Explicit getters for Scala template compatibility
    // Lombok @Getter may not work reliably with Scala templates during compilation
    public long getId() {
        return id;
    }
    
    public RAJobApplication getRaJobApplication() {
        return raJobApplication;
    }
    
    public User getFaculty() {
        return faculty;
    }
    
    public User getApplicant() {
        return applicant;
    }
    
    public RAJob getRaJob() {
        return raJob;
    }
    
    public String getInterviewDate() {
        return interviewDate;
    }
    
    public String getInterviewTime() {
        return interviewTime;
    }
    
    public String getMeetingLink() {
        return meetingLink;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getStudentResponse() {
        return studentResponse;
    }
    
    public String getNotes() {
        return notes;
    }
}

