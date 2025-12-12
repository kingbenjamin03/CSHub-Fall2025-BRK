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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = FacultyAvailability.class)
public class FacultyAvailability {
    private long id;
    private User faculty;
    private String startTime;
    private String endTime;
    private boolean isAvailable;
    private String createdTime;
    private String updatedTime;

    /*********************************************** Constructors *****************************************************/
    public FacultyAvailability() {
    }

    public FacultyAvailability(long id) {
        this.id = id;
    }

    /*********************************************** Utility methods **************************************************/

    /**
     * Deserializes the json to a FacultyAvailability.
     *
     * @param node the node to convert from.
     * @return the FacultyAvailability object.
     */
    public static FacultyAvailability deserialize(JsonNode node) throws Exception {
        try {
            if (node == null) {
                throw new NullPointerException("FacultyAvailability node should not be empty for FacultyAvailability.deserialize()");
            }
            if (node.get("id") == null) {
                return null;
            }

            FacultyAvailability availability = Json.fromJson(node, FacultyAvailability.class);
            return availability;

        } catch (Exception e) {
            Logger.debug("FacultyAvailability.deserialize() exception: " + e.toString());
            throw new Exception("FacultyAvailability.deserialize() exception: " + e.toString());
        }
    }

    /**
     * This utility method intends to return a list of FacultyAvailability from JsonNode.
     *
     * @param jsonArray the json array to convert from.
     * @return a list of FacultyAvailability objects.
     */
    public static List<FacultyAvailability> deserializeJsonToList(JsonNode jsonArray, int startIndex, int endIndex)
            throws Exception {
        List<FacultyAvailability> availabilityList = new ArrayList<FacultyAvailability>();
        for (int i = startIndex; i <= endIndex && i < jsonArray.size(); i++) {
            JsonNode json = jsonArray.path(i);
            FacultyAvailability availability = FacultyAvailability.deserialize(json);
            availabilityList.add(availability);
        }
        return availabilityList;
    }

    /*********************************************** Getters & Setters ************************************************/
    
    // Explicit getters for Scala template compatibility
    // Lombok @Getter may not work reliably with Scala templates during compilation
    public long getId() {
        return id;
    }
    
    public User getFaculty() {
        return faculty;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    /**
     * Explicit getter for isAvailable to ensure compatibility with Scala templates
     * Lombok generates isAvailable() but Scala templates may need getIsAvailable()
     */
    public boolean getIsAvailable() {
        return isAvailable;
    }
    
    public String getCreatedTime() {
        return createdTime;
    }
    
    public String getUpdatedTime() {
        return updatedTime;
    }
}

