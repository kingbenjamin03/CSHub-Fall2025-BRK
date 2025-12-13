package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import models.RAInterview;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.InterviewCalendarService;
import services.InterviewEmailService;
import services.RAInterviewService;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RAInterviewController extends Controller {

    private final RAInterviewService interviewService;
    private final InterviewEmailService emailService;
    private final InterviewCalendarService calendarService;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    Config config;

    @Inject
    public RAInterviewController(RAInterviewService interviewService,
                                 InterviewEmailService emailService,
                                 InterviewCalendarService calendarService) {
        this.interviewService = interviewService;
        this.emailService = emailService;
        this.calendarService = calendarService;
    }

    /**
     * Create a new interview
     *
     * @return created interview
     */
    public Result scheduleInterview() {
        try {
            JsonNode json = request().body().asJson();
            if (json == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Expecting JSON data");
                return badRequest(response);
            }

            if (!json.has("raJobApplicationId") || !json.has("facultyId") || !json.has("interviewDateTime")) {
                ObjectNode response = Json.newObject();
                response.put("error", "raJobApplicationId, facultyId, and interviewDateTime are required");
                return badRequest(response);
            }

            Long raJobApplicationId = json.get("raJobApplicationId").asLong();
            Long facultyId = json.get("facultyId").asLong();
            String interviewDateTimeStr = json.get("interviewDateTime").asText();
            String meetingLink = json.has("meetingLink") ? json.get("meetingLink").asText() : null;
            String location = json.has("location") ? json.get("location").asText() : null;
            String notes = json.has("notes") ? json.get("notes").asText() : null;

            Date interviewDateTime = DATE_FORMAT.parse(interviewDateTimeStr);

            RAInterview interview = interviewService.createInterview(raJobApplicationId, facultyId, interviewDateTime,
                    meetingLink, location, notes);

            if (interview == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Failed to create interview");
                return badRequest(response);
            }

            // Send email notification
            emailService.sendInterviewScheduledEmail(interview);

            return created(Json.toJson(interview));
        } catch (ParseException e) {
            Logger.error("RAInterviewController.scheduleInterview: Invalid date format", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            return badRequest(response);
        } catch (Exception e) {
            Logger.error("RAInterviewController.scheduleInterview: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to schedule interview");
            return internalServerError(response);
        }
    }

    /**
     * Get interview by ID
     *
     * @param interviewId the interview ID
     * @return interview details
     */
    public Result getInterviewById(Long interviewId) {
        try {
            RAInterview interview = interviewService.getInterviewById(interviewId);
            if (interview == null) {
                return notFound("Interview not found");
            }
            return ok(Json.toJson(interview));
        } catch (Exception e) {
            Logger.error("RAInterviewController.getInterviewById: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to retrieve interview");
            return internalServerError(response);
        }
    }

    /**
     * Get all interviews for a specific application
     *
     * @param applicationId the RA job application ID
     * @return list of interviews
     */
    public Result getInterviewsByApplicationId(Long applicationId) {
        try {
            List<RAInterview> interviews = interviewService.getInterviewsByApplicationId(applicationId);
            return ok(Json.toJson(interviews));
        } catch (Exception e) {
            Logger.error("RAInterviewController.getInterviewsByApplicationId: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to retrieve interviews");
            return internalServerError(response);
        }
    }

    /**
     * Get all interviews for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @return list of interviews
     */
    public Result getInterviewsByFacultyId(Long facultyId) {
        try {
            List<RAInterview> interviews = interviewService.getInterviewsByFacultyId(facultyId);
            return ok(Json.toJson(interviews));
        } catch (Exception e) {
            Logger.error("RAInterviewController.getInterviewsByFacultyId: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to retrieve interviews");
            return internalServerError(response);
        }
    }

    /**
     * Get all interviews for an applicant
     *
     * @param applicantId the applicant's user ID
     * @return list of interviews
     */
    public Result getInterviewsByApplicantId(Long applicantId) {
        try {
            List<RAInterview> interviews = interviewService.getInterviewsByApplicantId(applicantId);
            return ok(Json.toJson(interviews));
        } catch (Exception e) {
            Logger.error("RAInterviewController.getInterviewsByApplicantId: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to retrieve interviews");
            return internalServerError(response);
        }
    }

    /**
     * Reschedule an interview
     *
     * @param interviewId the interview ID
     * @return updated interview
     */
    public Result rescheduleInterview(Long interviewId) {
        try {
            JsonNode json = request().body().asJson();
            if (json == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Expecting JSON data");
                return badRequest(response);
            }

            if (!json.has("newDateTime")) {
                ObjectNode response = Json.newObject();
                response.put("error", "newDateTime is required");
                return badRequest(response);
            }

            String newDateTimeStr = json.get("newDateTime").asText();
            RAInterview existingInterview = interviewService.getInterviewById(interviewId);
            if (existingInterview == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Interview not found");
                return notFound(response);
            }
            Date oldDateTime = existingInterview.getInterviewTime();
            Date newDateTime = DATE_FORMAT.parse(newDateTimeStr);
            String meetingLink = json.has("meetingLink") ? json.get("meetingLink").asText() : null;
            String location = json.has("location") ? json.get("location").asText() : null;

            RAInterview interview = interviewService.rescheduleInterview(interviewId, newDateTime, meetingLink, location);
            if (interview == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Failed to reschedule interview");
                return badRequest(response);
            }

            // Send email notification
            emailService.sendInterviewRescheduledEmail(interview, oldDateTime);

            return ok(Json.toJson(interview));
        } catch (ParseException e) {
            Logger.error("RAInterviewController.rescheduleInterview: Invalid date format", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            return badRequest(response);
        } catch (Exception e) {
            Logger.error("RAInterviewController.rescheduleInterview: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to reschedule interview");
            return internalServerError(response);
        }
    }

    /**
     * Cancel an interview
     *
     * @param interviewId the interview ID
     * @return canceled interview
     */
    public Result cancelInterview(Long interviewId) {
        try {
            JsonNode json = request().body().asJson();
            String reason = json != null && json.has("reason") ? json.get("reason").asText() : null;

            RAInterview interview = interviewService.cancelInterview(interviewId, reason);
            if (interview == null) {
                ObjectNode response = Json.newObject();
                response.put("error", "Failed to cancel interview");
                return badRequest(response);
            }

            // Send email notification
            emailService.sendInterviewCanceledEmail(interview, reason);

            return ok(Json.toJson(interview));
        } catch (Exception e) {
            Logger.error("RAInterviewController.cancelInterview: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to cancel interview");
            return internalServerError(response);
        }
    }

    /**
     * Update student response to an interview
     *
     * @param interviewId the interview ID
     * @return updated interview
     */
    public Result updateStudentResponse(Long interviewId) {
        try {
            JsonNode json = request().body().asJson();
            if (json == null || !json.has("response")) {
                ObjectNode response = Json.newObject();
                response.put("error", "response is required");
                return badRequest(response);
            }

            String response = json.get("response").asText();
            RAInterview interview = interviewService.updateStudentResponse(interviewId, response);
            if (interview == null) {
                ObjectNode responseObj = Json.newObject();
                responseObj.put("error", "Failed to update student response");
                return badRequest(responseObj);
            }

            // Send email notification to faculty
            emailService.sendStudentResponseNotification(interview);

            return ok(Json.toJson(interview));
        } catch (Exception e) {
            Logger.error("RAInterviewController.updateStudentResponse: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to update student response");
            return internalServerError(response);
        }
    }

    /**
     * Download iCal file for an interview
     *
     * @param interviewId the interview ID
     * @return iCal file
     */
    public Result downloadICal(Long interviewId) {
        try {
            RAInterview interview = interviewService.getInterviewById(interviewId);
            if (interview == null) {
                return notFound("Interview not found");
            }

            String icsContent = calendarService.generateICalEvent(interview);
            return ok(icsContent).as("text/calendar");
        } catch (Exception e) {
            Logger.error("RAInterviewController.downloadICal: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to generate iCal file");
            return internalServerError(response);
        }
    }

    /**
     * Get Google Calendar link for an interview
     *
     * @param interviewId the interview ID
     * @return Google Calendar URL
     */
    public Result getGoogleCalendarLink(Long interviewId) {
        try {
            RAInterview interview = interviewService.getInterviewById(interviewId);
            if (interview == null) {
                return notFound("Interview not found");
            }

            String link = calendarService.generateGoogleCalendarLink(interview);
            ObjectNode response = Json.newObject();
            response.put("link", link);
            return ok(response);
        } catch (Exception e) {
            Logger.error("RAInterviewController.getGoogleCalendarLink: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to generate Google Calendar link");
            return internalServerError(response);
        }
    }

    /**
     * Get Outlook Calendar link for an interview
     *
     * @param interviewId the interview ID
     * @return Outlook Calendar URL
     */
    public Result getOutlookCalendarLink(Long interviewId) {
        try {
            RAInterview interview = interviewService.getInterviewById(interviewId);
            if (interview == null) {
                return notFound("Interview not found");
            }

            String link = calendarService.generateOutlookCalendarLink(interview);
            ObjectNode response = Json.newObject();
            response.put("link", link);
            return ok(response);
        } catch (Exception e) {
            Logger.error("RAInterviewController.getOutlookCalendarLink: Error", e);
            ObjectNode response = Json.newObject();
            response.put("error", "Failed to generate Outlook Calendar link");
            return internalServerError(response);
        }
    }
}

