package services;

import models.RAInterview;
import models.RAJobApplication;
import models.RAJob;
import models.User;
import play.Logger;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class RAInterviewService {

    /**
     * Create a new interview
     *
     * @param raJobApplicationId the RA job application ID
     * @param facultyId the faculty member's user ID
     * @param interviewDateTime the interview date and time
     * @param meetingLink optional meeting link
     * @param location optional location
     * @param notes optional notes
     * @return the created interview, or null if creation failed
     */
    public RAInterview createInterview(Long raJobApplicationId, Long facultyId, Date interviewDateTime,
                                       String meetingLink, String location, String notes) {
        RAJobApplication application = RAJobApplication.find.byId(raJobApplicationId);
        if (application == null) {
            Logger.error("RAInterviewService.createInterview: Application not found with ID: " + raJobApplicationId);
            return null;
        }

        User faculty = User.find.byId(facultyId);
        if (faculty == null) {
            Logger.error("RAInterviewService.createInterview: Faculty not found with ID: " + facultyId);
            return null;
        }

        // Verify faculty is the publisher of the RA job
        if (application.getAppliedRAJob() == null 
                || application.getAppliedRAJob().getRajobPublisher() == null
                || application.getAppliedRAJob().getRajobPublisher().getId() != facultyId) {
            Logger.error("RAInterviewService.createInterview: Faculty is not the publisher of this RA job");
            return null;
        }

        // Check for conflicts
        FacultyAvailabilityService availabilityService = new FacultyAvailabilityService();
        Date endTime = new Date(interviewDateTime.getTime() + 3600000); // Assume 1 hour duration
        if (availabilityService.checkSlotConflict(facultyId, interviewDateTime, endTime)) {
            Logger.error("RAInterviewService.createInterview: Interview time conflicts with existing schedule");
            return null;
        }

        RAInterview interview = new RAInterview();
        interview.setRaJobApplication(application);
        interview.setFaculty(faculty);
        interview.setApplicant(application.getApplicant());
        interview.setRaJob(application.getAppliedRAJob());
        interview.setInterviewDate(interviewDateTime);
        interview.setInterviewTime(interviewDateTime);
        interview.setMeetingLink(meetingLink);
        interview.setLocation(location);
        interview.setNotes(notes);
        interview.setStatus("pending");
        interview.setStudentResponse(null);
        interview.save();

        return interview;
    }

    /**
     * Get interview by ID
     *
     * @param interviewId the interview ID
     * @return the interview, or null if not found
     */
    public RAInterview getInterviewById(Long interviewId) {
        return RAInterview.find.query()
                .fetch("faculty")
                .fetch("applicant")
                .fetch("raJobApplication")
                .fetch("raJob")
                .where().eq("id", interviewId).findOne();
    }

    /**
     * Get all interviews for a specific application
     *
     * @param raJobApplicationId the RA job application ID
     * @return list of interviews
     */
    public List<RAInterview> getInterviewsByApplicationId(Long raJobApplicationId) {
        try {
            return RAInterview.find.query()
                    .fetch("faculty")
                    .fetch("applicant")
                    .fetch("raJobApplication")
                    .fetch("raJob")
                    .where()
                    .eq("rajob_application_id", raJobApplicationId)
                    .orderBy("interviewTime desc")
                    .findList();
        } catch (Exception e) {
            Logger.error("RAInterviewService.getInterviewsByApplicationId: Error querying interviews for application " + raJobApplicationId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Get all interviews for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @return list of interviews
     */
    public List<RAInterview> getInterviewsByFacultyId(Long facultyId) {
        return RAInterview.find.query()
                .fetch("faculty")
                .fetch("applicant")
                .fetch("raJobApplication")
                .fetch("raJob")
                .where()
                .eq("faculty.id", facultyId)
                .orderBy("interviewTime desc")
                .findList();
    }

    /**
     * Get all interviews for an applicant
     *
     * @param applicantId the applicant's user ID
     * @return list of interviews
     */
    public List<RAInterview> getInterviewsByApplicantId(Long applicantId) {
        return RAInterview.find.query()
                .fetch("faculty")
                .fetch("applicant")
                .fetch("raJobApplication")
                .fetch("raJob")
                .where()
                .eq("applicant.id", applicantId)
                .orderBy("interviewTime desc")
                .findList();
    }

    /**
     * Reschedule an interview
     *
     * @param interviewId the interview ID
     * @param newDateTime the new interview date and time
     * @param meetingLink optional new meeting link
     * @param location optional new location
     * @return the updated interview, or null if update failed
     */
    public RAInterview rescheduleInterview(Long interviewId, Date newDateTime, String meetingLink, String location) {
        RAInterview interview = RAInterview.find.byId(interviewId);
        if (interview == null) {
            Logger.error("RAInterviewService.rescheduleInterview: Interview not found with ID: " + interviewId);
            return null;
        }

        // Check for conflicts
        FacultyAvailabilityService availabilityService = new FacultyAvailabilityService();
        Date endTime = new Date(newDateTime.getTime() + 3600000); // Assume 1 hour duration
        if (availabilityService.checkSlotConflict(interview.getFaculty().getId(), newDateTime, endTime)) {
            Logger.error("RAInterviewService.rescheduleInterview: New interview time conflicts with existing schedule");
            return null;
        }

        interview.setInterviewDate(newDateTime);
        interview.setInterviewTime(newDateTime);
        if (meetingLink != null) {
            interview.setMeetingLink(meetingLink);
        }
        if (location != null) {
            interview.setLocation(location);
        }
        interview.setStatus("rescheduled");
        interview.setStudentResponse(null); // Reset student response
        interview.save();

        return interview;
    }

    /**
     * Cancel an interview
     *
     * @param interviewId the interview ID
     * @param reason optional cancellation reason
     * @return the canceled interview, or null if not found
     */
    public RAInterview cancelInterview(Long interviewId, String reason) {
        RAInterview interview = RAInterview.find.byId(interviewId);
        if (interview == null) {
            Logger.error("RAInterviewService.cancelInterview: Interview not found with ID: " + interviewId);
            return null;
        }

        interview.setStatus("canceled");
        if (reason != null && !reason.isEmpty()) {
            String currentNotes = interview.getNotes() != null ? interview.getNotes() : "";
            interview.setNotes(currentNotes + "\n\nCancellation reason: " + reason);
        }
        interview.save();

        return interview;
    }

    /**
     * Update student response to an interview
     *
     * @param interviewId the interview ID
     * @param response the student response (accepted, declined, reschedule_requested)
     * @return the updated interview, or null if not found
     */
    public RAInterview updateStudentResponse(Long interviewId, String response) {
        RAInterview interview = RAInterview.find.byId(interviewId);
        if (interview == null) {
            Logger.error("RAInterviewService.updateStudentResponse: Interview not found with ID: " + interviewId);
            return null;
        }

        if (!response.equals("accepted") && !response.equals("declined") && !response.equals("reschedule_requested")) {
            Logger.error("RAInterviewService.updateStudentResponse: Invalid response: " + response);
            return null;
        }

        interview.setStudentResponse(response);
        
        if (response.equals("accepted")) {
            interview.setStatus("confirmed");
        } else if (response.equals("declined")) {
            interview.setStatus("declined");
        } else {
            // reschedule_requested - keep status as pending or rescheduled
            if (interview.getStatus().equals("pending")) {
                interview.setStatus("pending");
            } else {
                interview.setStatus("rescheduled");
            }
        }
        
        interview.save();

        return interview;
    }

    /**
     * Confirm an interview (after student accepts)
     *
     * @param interviewId the interview ID
     * @return the confirmed interview, or null if not found
     */
    public RAInterview confirmInterview(Long interviewId) {
        RAInterview interview = RAInterview.find.byId(interviewId);
        if (interview == null) {
            Logger.error("RAInterviewService.confirmInterview: Interview not found with ID: " + interviewId);
            return null;
        }

        interview.setStatus("confirmed");
        interview.setStudentResponse("accepted");
        interview.save();

        return interview;
    }
}

