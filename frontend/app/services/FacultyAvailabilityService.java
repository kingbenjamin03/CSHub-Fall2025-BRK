package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import models.FacultyAvailability;
import play.Logger;
import play.libs.Json;
import utils.RESTfulCalls;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FacultyAvailabilityService {
    @Inject
    Config config;

    /**
     * Get all availability slots for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @return list of availability slots
     */
    public List<FacultyAvailability> getFacultyAvailability(Long facultyId) {
        List<FacultyAvailability> availabilityList = new ArrayList<>();
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/faculty/availability/" + facultyId)
            );
            if (response.has("error")) {
                Logger.debug("FacultyAvailabilityService.getFacultyAvailability() error: " + response.get("error").asText());
                return availabilityList;
            }

            if (response.isArray()) {
                for (JsonNode node : response) {
                    FacultyAvailability availability = FacultyAvailability.deserialize(node);
                    availabilityList.add(availability);
                }
            }
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityService.getFacultyAvailability() exception: " + e.toString(), e);
        }
        return availabilityList;
    }

    /**
     * Get available slots for a faculty member within a date range
     *
     * @param facultyId the faculty member's user ID
     * @param startDate start date string (yyyy-MM-dd HH:mm:ss)
     * @param endDate end date string (yyyy-MM-dd HH:mm:ss)
     * @return list of available slots
     */
    public List<FacultyAvailability> getAvailableSlotsForScheduling(Long facultyId, String startDate, String endDate) {
        List<FacultyAvailability> availabilityList = new ArrayList<>();
        try {
            ObjectNode jsonData = Json.newObject();
            jsonData.put("startDate", startDate);
            jsonData.put("endDate", endDate);

            JsonNode response = RESTfulCalls.postAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/faculty/availability/" + facultyId + "/availableSlots"),
                    jsonData
            );
            if (response.has("error")) {
                Logger.debug("FacultyAvailabilityService.getAvailableSlotsForScheduling() error: " + response.get("error").asText());
                return availabilityList;
            }

            if (response.isArray()) {
                for (JsonNode node : response) {
                    FacultyAvailability availability = FacultyAvailability.deserialize(node);
                    availabilityList.add(availability);
                }
            }
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityService.getAvailableSlotsForScheduling() exception: " + e.toString(), e);
        }
        return availabilityList;
    }

    /**
     * Add a new availability slot
     *
     * @param facultyId the faculty member's user ID
     * @param startTime start time string (yyyy-MM-dd HH:mm:ss)
     * @param endTime end time string (yyyy-MM-dd HH:mm:ss)
     * @return created availability slot, or null if failed
     */
    public FacultyAvailability addAvailabilitySlot(Long facultyId, String startTime, String endTime) {
        try {
            ObjectNode jsonData = Json.newObject();
            jsonData.put("facultyId", facultyId);
            jsonData.put("startTime", startTime);
            jsonData.put("endTime", endTime);

            JsonNode response = RESTfulCalls.postAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/faculty/availability"),
                    jsonData
            );
            if (response.has("error")) {
                Logger.debug("FacultyAvailabilityService.addAvailabilitySlot() error: " + response.get("error").asText());
                return null;
            }

            return FacultyAvailability.deserialize(response);
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityService.addAvailabilitySlot() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Update an existing availability slot
     *
     * @param slotId the availability slot ID
     * @param startTime new start time string (yyyy-MM-dd HH:mm:ss)
     * @param endTime new end time string (yyyy-MM-dd HH:mm:ss)
     * @return updated availability slot, or null if failed
     */
    public FacultyAvailability updateAvailabilitySlot(Long slotId, String startTime, String endTime) {
        try {
            ObjectNode jsonData = Json.newObject();
            jsonData.put("startTime", startTime);
            jsonData.put("endTime", endTime);
            // Note: facultyId is needed for conflict checking but will be extracted from session on backend

            JsonNode response = RESTfulCalls.putAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/faculty/availability/" + slotId),
                    jsonData
            );
            if (response.has("error")) {
                Logger.debug("FacultyAvailabilityService.updateAvailabilitySlot() error: " + response.get("error").asText());
                return null;
            }

            return FacultyAvailability.deserialize(response);
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityService.updateAvailabilitySlot() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Delete an availability slot
     *
     * @param slotId the availability slot ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteAvailabilitySlot(Long slotId) {
        try {
            JsonNode response = RESTfulCalls.deleteAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/faculty/availability/" + slotId)
            );
            if (response.has("error")) {
                Logger.debug("FacultyAvailabilityService.deleteAvailabilitySlot() error: " + response.get("error").asText());
                return false;
            }

            return true;
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityService.deleteAvailabilitySlot() exception: " + e.toString(), e);
            return false;
        }
    }
}

