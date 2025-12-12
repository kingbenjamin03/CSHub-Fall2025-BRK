package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import models.RAInterview;
import play.Logger;
import play.libs.Json;
import utils.RESTfulCalls;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RAInterviewService {
    @Inject
    Config config;

    /**
     * Schedule a new interview
     *
     * @param raJobApplicationId the RA job application ID
     * @param facultyId the faculty member's user ID
     * @param interviewDateTime interview date and time string (yyyy-MM-dd HH:mm:ss)
     * @param meetingLink optional meeting link
     * @param location optional location
     * @param notes optional notes
     * @return created interview, or null if failed
     */
    public RAInterview scheduleInterview(Long raJobApplicationId, Long facultyId, String interviewDateTime,
                                        String meetingLink, String location, String notes) {
        try {
            ObjectNode jsonData = Json.newObject();
            jsonData.put("raJobApplicationId", raJobApplicationId);
            jsonData.put("facultyId", facultyId);
            jsonData.put("interviewDateTime", interviewDateTime);
            if (meetingLink != null && !meetingLink.isEmpty()) {
                jsonData.put("meetingLink", meetingLink);
            }
            if (location != null && !location.isEmpty()) {
                jsonData.put("location", location);
            }
            if (notes != null && !notes.isEmpty()) {
                jsonData.put("notes", notes);
            }

            JsonNode response = RESTfulCalls.postAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/schedule"),
                    jsonData
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.scheduleInterview() error: " + response.get("error").asText());
                return null;
            }

            return RAInterview.deserialize(response);
        } catch (Exception e) {
            Logger.error("RAInterviewService.scheduleInterview() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Get interview by ID
     *
     * @param interviewId the interview ID
     * @return interview details, or null if not found
     */
    public RAInterview getInterviewDetails(Long interviewId) {
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/" + interviewId)
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.getInterviewDetails() error: " + response.get("error").asText());
                return null;
            }

            return RAInterview.deserialize(response);
        } catch (Exception e) {
            Logger.error("RAInterviewService.getInterviewDetails() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Get all interviews for a specific application
     *
     * @param applicationId the RA job application ID
     * @return list of interviews
     */
    public List<RAInterview> getInterviewsByApplicationId(Long applicationId) {
        List<RAInterview> interviewList = new ArrayList<>();
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/application/" + applicationId)
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.getInterviewsByApplicationId() error: " + response.get("error").asText());
                return interviewList;
            }

            if (response.isArray()) {
                for (JsonNode node : response) {
                    RAInterview interview = RAInterview.deserialize(node);
                    interviewList.add(interview);
                }
            }
        } catch (Exception e) {
            Logger.error("RAInterviewService.getInterviewsByApplicationId() exception: " + e.toString(), e);
        }
        return interviewList;
    }

    /**
     * Get all interviews for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @return list of interviews
     */
    public List<RAInterview> getInterviewsByFacultyId(Long facultyId) {
        List<RAInterview> interviewList = new ArrayList<>();
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/faculty/" + facultyId)
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.getInterviewsByFacultyId() error: " + response.get("error").asText());
                return interviewList;
            }

            if (response.isArray()) {
                for (JsonNode node : response) {
                    RAInterview interview = RAInterview.deserialize(node);
                    interviewList.add(interview);
                }
            }
        } catch (Exception e) {
            Logger.error("RAInterviewService.getInterviewsByFacultyId() exception: " + e.toString(), e);
        }
        return interviewList;
    }

    /**
     * Get all interviews for an applicant
     *
     * @param applicantId the applicant's user ID
     * @return list of interviews
     */
    public List<RAInterview> getInterviewsByApplicantId(Long applicantId) {
        List<RAInterview> interviewList = new ArrayList<>();
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/applicant/" + applicantId)
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.getInterviewsByApplicantId() error: " + response.get("error").asText());
                return interviewList;
            }

            if (response.isArray()) {
                for (JsonNode node : response) {
                    RAInterview interview = RAInterview.deserialize(node);
                    interviewList.add(interview);
                }
            }
        } catch (Exception e) {
            Logger.error("RAInterviewService.getInterviewsByApplicantId() exception: " + e.toString(), e);
        }
        return interviewList;
    }

    /**
     * Reschedule an interview
     *
     * @param interviewId the interview ID
     * @param newDateTime new interview date and time string (yyyy-MM-dd HH:mm:ss)
     * @param meetingLink optional new meeting link
     * @param location optional new location
     * @return updated interview, or null if failed
     */
    public RAInterview rescheduleInterview(Long interviewId, String newDateTime, String meetingLink, String location) {
        try {
            ObjectNode jsonData = Json.newObject();
            jsonData.put("newDateTime", newDateTime);
            if (meetingLink != null && !meetingLink.isEmpty()) {
                jsonData.put("meetingLink", meetingLink);
            }
            if (location != null && !location.isEmpty()) {
                jsonData.put("location", location);
            }

            JsonNode response = RESTfulCalls.putAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/" + interviewId + "/reschedule"),
                    jsonData
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.rescheduleInterview() error: " + response.get("error").asText());
                return null;
            }

            return RAInterview.deserialize(response);
        } catch (Exception e) {
            Logger.error("RAInterviewService.rescheduleInterview() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Cancel an interview
     *
     * @param interviewId the interview ID
     * @param reason optional cancellation reason
     * @return canceled interview, or null if failed
     */
    public RAInterview cancelInterview(Long interviewId, String reason) {
        try {
            ObjectNode jsonData = Json.newObject();
            if (reason != null && !reason.isEmpty()) {
                jsonData.put("reason", reason);
            }

            JsonNode response = RESTfulCalls.putAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/" + interviewId + "/cancel"),
                    jsonData
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.cancelInterview() error: " + response.get("error").asText());
                return null;
            }

            return RAInterview.deserialize(response);
        } catch (Exception e) {
            Logger.error("RAInterviewService.cancelInterview() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Submit student response to an interview
     *
     * @param interviewId the interview ID
     * @param response the student response (accepted, declined, reschedule_requested)
     * @return updated interview, or null if failed
     */
    public RAInterview submitStudentResponse(Long interviewId, String response) {
        try {
            ObjectNode jsonData = Json.newObject();
            jsonData.put("response", response);

            JsonNode apiResponse = RESTfulCalls.putAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/" + interviewId + "/studentResponse"),
                    jsonData
            );
            if (apiResponse.has("error")) {
                Logger.debug("RAInterviewService.submitStudentResponse() error: " + apiResponse.get("error").asText());
                return null;
            }

            return RAInterview.deserialize(apiResponse);
        } catch (Exception e) {
            Logger.error("RAInterviewService.submitStudentResponse() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Get Google Calendar link for an interview
     *
     * @param interviewId the interview ID
     * @return Google Calendar URL, or null if failed
     */
    public String getGoogleCalendarLink(Long interviewId) {
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/" + interviewId + "/google-calendar")
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.getGoogleCalendarLink() error: " + response.get("error").asText());
                return null;
            }

            return response.has("link") ? response.get("link").asText() : null;
        } catch (Exception e) {
            Logger.error("RAInterviewService.getGoogleCalendarLink() exception: " + e.toString(), e);
            return null;
        }
    }

    /**
     * Get Outlook Calendar link for an interview
     *
     * @param interviewId the interview ID
     * @return Outlook Calendar URL, or null if failed
     */
    public String getOutlookCalendarLink(Long interviewId) {
        try {
            JsonNode response = RESTfulCalls.getAPI(
                    RESTfulCalls.getBackendAPIUrl(config, "/rajob/interview/" + interviewId + "/outlook-calendar")
            );
            if (response.has("error")) {
                Logger.debug("RAInterviewService.getOutlookCalendarLink() error: " + response.get("error").asText());
                return null;
            }

            return response.has("link") ? response.get("link").asText() : null;
        } catch (Exception e) {
            Logger.error("RAInterviewService.getOutlookCalendarLink() exception: " + e.toString(), e);
            return null;
        }
    }
}

