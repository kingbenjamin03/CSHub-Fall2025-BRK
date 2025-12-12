package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.FacultyAvailability;
import models.User;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.FacultyAvailabilityService;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FacultyAvailabilityController extends Controller {

    private final FacultyAvailabilityService availabilityService;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    public FacultyAvailabilityController(FacultyAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * Get all availability slots for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @return list of availability slots
     */
    public Result getAvailabilityByFacultyId(Long facultyId) {
        try {
            List<FacultyAvailability> availabilityList = availabilityService.getAvailabilityByFacultyId(facultyId);
            return ok(Json.toJson(availabilityList));
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityController.getAvailabilityByFacultyId: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to retrieve availability");
            return internalServerError(response);
        }
    }

    /**
     * Get available slots for a faculty member within a date range
     *
     * @param facultyId the faculty member's user ID
     * @return list of available slots
     */
    public Result getAvailableSlots(Long facultyId) {
        try {
            JsonNode json = request().body().asJson();
            if (json == null || !json.has("startDate") || !json.has("endDate")) {
                ObjectNode response = Json.newObject();
                response.put("error", "startDate and endDate are required");
                return badRequest(response);
            }

            String startDateStr = json.get("startDate").asText();
            String endDateStr = json.get("endDate").asText();

            Date startDate = DATE_FORMAT.parse(startDateStr);
            Date endDate = DATE_FORMAT.parse(endDateStr);

            List<FacultyAvailability> availabilityList = availabilityService.getAvailableSlots(facultyId, startDate, endDate);
            return ok(Json.toJson(availabilityList));
        } catch (ParseException e) {
            Logger.error("FacultyAvailabilityController.getAvailableSlots: Invalid date format", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            return badRequest(response);
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityController.getAvailableSlots: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to retrieve available slots");
            return internalServerError(response);
        }
    }

    /**
     * Add a new availability slot
     *
     * @return created availability slot
     */
    public Result addAvailabilitySlot() {
        try {
            JsonNode json = request().body().asJson();
            if (json == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Expecting JSON data");
                return badRequest(response);
            }

            if (!json.has("facultyId") || !json.has("startTime") || !json.has("endTime")) {
                ObjectNode response = Json.newObject();
                response.put("error", "facultyId, startTime, and endTime are required");
                return badRequest(response);
            }

            Long facultyId = json.get("facultyId").asLong();
            String startTimeStr = json.get("startTime").asText();
            String endTimeStr = json.get("endTime").asText();

            Date startTime = DATE_FORMAT.parse(startTimeStr);
            Date endTime = DATE_FORMAT.parse(endTimeStr);

            FacultyAvailability availability = availabilityService.addAvailabilitySlot(facultyId, startTime, endTime);
            if (availability == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Failed to add availability slot");
                return badRequest(response);
            }

            return created(Json.toJson(availability));
        } catch (ParseException e) {
            Logger.error("FacultyAvailabilityController.addAvailabilitySlot: Invalid date format", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            return badRequest(response);
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityController.addAvailabilitySlot: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to add availability slot");
            return internalServerError(response);
        }
    }

    /**
     * Update an existing availability slot
     *
     * @param slotId the availability slot ID
     * @return updated availability slot
     */
    public Result updateAvailabilitySlot(Long slotId) {
        try {
            JsonNode json = request().body().asJson();
            if (json == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Expecting JSON data");
                return badRequest(response);
            }

            if (!json.has("startTime") || !json.has("endTime")) {
                ObjectNode response = Json.newObject();
                response.put("error", "startTime and endTime are required");
                return badRequest(response);
            }

            String startTimeStr = json.get("startTime").asText();
            String endTimeStr = json.get("endTime").asText();

            Date startTime = DATE_FORMAT.parse(startTimeStr);
            Date endTime = DATE_FORMAT.parse(endTimeStr);

            FacultyAvailability availability = availabilityService.updateAvailabilitySlot(slotId, startTime, endTime);
            if (availability == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Failed to update availability slot");
                return badRequest(response);
            }

            return ok(Json.toJson(availability));
        } catch (ParseException e) {
            Logger.error("FacultyAvailabilityController.updateAvailabilitySlot: Invalid date format", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            return badRequest(response);
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityController.updateAvailabilitySlot: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to update availability slot");
            return internalServerError(response);
        }
    }

    /**
     * Delete an availability slot
     *
     * @param slotId the availability slot ID
     * @return success or error message
     */
    public Result deleteAvailabilitySlot(Long slotId) {
        try {
            boolean deleted = availabilityService.deleteAvailabilitySlot(slotId);
            if (!deleted) {
                ObjectNode response = Json.newObject();
                response.put("error", "Failed to delete availability slot");
                return badRequest(response);
            }

            ObjectNode response = Json.newObject();
            response.put("message", "Availability slot deleted successfully");
            return ok(response);
        } catch (Exception e) {
            Logger.error("FacultyAvailabilityController.deleteAvailabilitySlot: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to delete availability slot");
            return internalServerError(response);
        }
    }
}

