package services;

import com.typesafe.config.Config;
import models.InterviewNotification;
import models.RAInterview;
import play.Logger;
import utils.EmailUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class InterviewEmailService {

    private final Config config;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a");

    @Inject
    public InterviewEmailService(Config config) {
        this.config = config;
    }

    /**
     * Send email notification when an interview is scheduled
     *
     * @param interview the interview that was scheduled
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendInterviewScheduledEmail(RAInterview interview) {
        if (!EmailUtils.isEmailEnabled(config)) {
            Logger.info("InterviewEmailService.sendInterviewScheduledEmail: Email is disabled");
            return false;
        }

        try {
            String recipient = interview.getApplicant().getEmail();
            String subject = "Interview Scheduled - " + interview.getRaJob().getTitle();
            String body = getInterviewScheduledEmailBody(interview);

            EmailUtils.sendIndividualEmail(config, recipient, subject, body);
            
            // Save notification record
            saveNotification(interview, "scheduled", interview.getApplicant().getId(), subject, body);
            
            return true;
        } catch (Exception e) {
            Logger.error("InterviewEmailService.sendInterviewScheduledEmail: Failed to send email", e);
            return false;
        }
    }

    /**
     * Send email notification when an interview is rescheduled
     *
     * @param interview the interview that was rescheduled
     * @param oldDateTime the previous interview date/time
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendInterviewRescheduledEmail(RAInterview interview, Date oldDateTime) {
        if (!EmailUtils.isEmailEnabled(config)) {
            Logger.info("InterviewEmailService.sendInterviewRescheduledEmail: Email is disabled");
            return false;
        }

        try {
            String recipient = interview.getApplicant().getEmail();
            String subject = "Interview Rescheduled - " + interview.getRaJob().getTitle();
            String body = getInterviewRescheduledEmailBody(interview, oldDateTime);

            EmailUtils.sendIndividualEmail(config, recipient, subject, body);
            
            // Save notification record
            saveNotification(interview, "rescheduled", interview.getApplicant().getId(), subject, body);
            
            // Also notify faculty
            String facultySubject = "Interview Rescheduled - " + interview.getRaJob().getTitle();
            String facultyBody = getInterviewRescheduledEmailBodyForFaculty(interview, oldDateTime);
            EmailUtils.sendIndividualEmail(config, interview.getFaculty().getEmail(), facultySubject, facultyBody);
            saveNotification(interview, "rescheduled", interview.getFaculty().getId(), facultySubject, facultyBody);
            
            return true;
        } catch (Exception e) {
            Logger.error("InterviewEmailService.sendInterviewRescheduledEmail: Failed to send email", e);
            return false;
        }
    }

    /**
     * Send email notification when an interview is canceled
     *
     * @param interview the interview that was canceled
     * @param reason optional cancellation reason
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendInterviewCanceledEmail(RAInterview interview, String reason) {
        if (!EmailUtils.isEmailEnabled(config)) {
            Logger.info("InterviewEmailService.sendInterviewCanceledEmail: Email is disabled");
            return false;
        }

        try {
            String recipient = interview.getApplicant().getEmail();
            String subject = "Interview Canceled - " + interview.getRaJob().getTitle();
            String body = getInterviewCanceledEmailBody(interview, reason);

            EmailUtils.sendIndividualEmail(config, recipient, subject, body);
            
            // Save notification record
            saveNotification(interview, "canceled", interview.getApplicant().getId(), subject, body);
            
            return true;
        } catch (Exception e) {
            Logger.error("InterviewEmailService.sendInterviewCanceledEmail: Failed to send email", e);
            return false;
        }
    }

    /**
     * Send email notification to faculty when student responds to interview
     *
     * @param interview the interview with student response
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendStudentResponseNotification(RAInterview interview) {
        if (!EmailUtils.isEmailEnabled(config)) {
            Logger.info("InterviewEmailService.sendStudentResponseNotification: Email is disabled");
            return false;
        }

        try {
            String recipient = interview.getFaculty().getEmail();
            String subject = "Student Response - Interview for " + interview.getRaJob().getTitle();
            String body = getStudentResponseEmailBody(interview);

            EmailUtils.sendIndividualEmail(config, recipient, subject, body);
            
            // Save notification record
            saveNotification(interview, "student_response", interview.getFaculty().getId(), subject, body);
            
            return true;
        } catch (Exception e) {
            Logger.error("InterviewEmailService.sendStudentResponseNotification: Failed to send email", e);
            return false;
        }
    }

    /**
     * Send interview reminder email (can be called by scheduled job)
     *
     * @param interview the interview to remind about
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendInterviewReminderEmail(RAInterview interview) {
        if (!EmailUtils.isEmailEnabled(config)) {
            Logger.info("InterviewEmailService.sendInterviewReminderEmail: Email is disabled");
            return false;
        }

        try {
            // Send to both student and faculty
            String subject = "Interview Reminder - " + interview.getRaJob().getTitle();
            String body = getInterviewReminderEmailBody(interview);

            EmailUtils.sendIndividualEmail(config, interview.getApplicant().getEmail(), subject, body);
            EmailUtils.sendIndividualEmail(config, interview.getFaculty().getEmail(), subject, body);
            
            // Save notification records
            saveNotification(interview, "reminder", interview.getApplicant().getId(), subject, body);
            saveNotification(interview, "reminder", interview.getFaculty().getId(), subject, body);
            
            return true;
        } catch (Exception e) {
            Logger.error("InterviewEmailService.sendInterviewReminderEmail: Failed to send email", e);
            return false;
        }
    }

    /**
     * Get email body for interview scheduled notification
     */
    private String getInterviewScheduledEmailBody(RAInterview interview) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(interview.getApplicant().getFirstName()).append(",\n\n");
        body.append("An interview has been scheduled for the RA position: ").append(interview.getRaJob().getTitle()).append("\n\n");
        body.append("Interview Details:\n");
        body.append("Date & Time: ").append(DATETIME_FORMAT.format(interview.getInterviewTime())).append("\n");
        
        if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
            body.append("Location: ").append(interview.getLocation()).append("\n");
        }
        if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
            body.append("Meeting Link: ").append(interview.getMeetingLink()).append("\n");
        }
        
        body.append("\nFaculty: ").append(interview.getFaculty().getFirstName())
            .append(" ").append(interview.getFaculty().getLastName()).append("\n");
        body.append("Faculty Email: ").append(interview.getFaculty().getEmail()).append("\n\n");
        
        if (interview.getNotes() != null && !interview.getNotes().isEmpty()) {
            body.append("Additional Notes:\n").append(interview.getNotes()).append("\n\n");
        }
        
        body.append("Please respond by:\n");
        body.append("- Accepting the interview\n");
        body.append("- Declining the interview\n");
        body.append("- Requesting a different time\n\n");
        
        // In a real implementation, you would include links to the response page
        body.append("You can respond by logging into the system and viewing your interview details.\n\n");
        body.append("Best regards,\n");
        body.append("CSHub System");
        
        return body.toString();
    }

    /**
     * Get email body for interview rescheduled notification
     */
    private String getInterviewRescheduledEmailBody(RAInterview interview, Date oldDateTime) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(interview.getApplicant().getFirstName()).append(",\n\n");
        body.append("The interview for the RA position: ").append(interview.getRaJob().getTitle())
            .append(" has been rescheduled.\n\n");
        body.append("Previous Time: ").append(DATETIME_FORMAT.format(oldDateTime)).append("\n");
        body.append("New Time: ").append(DATETIME_FORMAT.format(interview.getInterviewTime())).append("\n\n");
        
        if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
            body.append("Location: ").append(interview.getLocation()).append("\n");
        }
        if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
            body.append("Meeting Link: ").append(interview.getMeetingLink()).append("\n");
        }
        
        body.append("\nPlease confirm your availability for the new time.\n\n");
        body.append("Best regards,\n");
        body.append("CSHub System");
        
        return body.toString();
    }

    /**
     * Get email body for interview rescheduled notification (for faculty)
     */
    private String getInterviewRescheduledEmailBodyForFaculty(RAInterview interview, Date oldDateTime) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(interview.getFaculty().getFirstName()).append(",\n\n");
        body.append("You have rescheduled the interview for the RA position: ").append(interview.getRaJob().getTitle()).append("\n\n");
        body.append("Applicant: ").append(interview.getApplicant().getFirstName())
            .append(" ").append(interview.getApplicant().getLastName()).append("\n");
        body.append("Previous Time: ").append(DATETIME_FORMAT.format(oldDateTime)).append("\n");
        body.append("New Time: ").append(DATETIME_FORMAT.format(interview.getInterviewTime())).append("\n\n");
        body.append("The applicant has been notified of the change.\n\n");
        body.append("Best regards,\n");
        body.append("CSHub System");
        
        return body.toString();
    }

    /**
     * Get email body for interview canceled notification
     */
    private String getInterviewCanceledEmailBody(RAInterview interview, String reason) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(interview.getApplicant().getFirstName()).append(",\n\n");
        body.append("The interview for the RA position: ").append(interview.getRaJob().getTitle())
            .append(" has been canceled.\n\n");
        
        if (reason != null && !reason.isEmpty()) {
            body.append("Reason: ").append(reason).append("\n\n");
        }
        
        body.append("If you have any questions, please contact the faculty member directly.\n\n");
        body.append("Best regards,\n");
        body.append("CSHub System");
        
        return body.toString();
    }

    /**
     * Get email body for student response notification
     */
    private String getStudentResponseEmailBody(RAInterview interview) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(interview.getFaculty().getFirstName()).append(",\n\n");
        body.append("The student ").append(interview.getApplicant().getFirstName())
            .append(" ").append(interview.getApplicant().getLastName())
            .append(" has responded to the interview invitation for: ").append(interview.getRaJob().getTitle()).append("\n\n");
        
        body.append("Response: ");
        if ("accepted".equals(interview.getStudentResponse())) {
            body.append("Accepted\n");
            body.append("The interview is confirmed for: ").append(DATETIME_FORMAT.format(interview.getInterviewTime())).append("\n");
        } else if ("declined".equals(interview.getStudentResponse())) {
            body.append("Declined\n");
        } else if ("reschedule_requested".equals(interview.getStudentResponse())) {
            body.append("Requested Reschedule\n");
            body.append("Please log into the system to view the request and reschedule if needed.\n");
        }
        
        body.append("\nBest regards,\n");
        body.append("CSHub System");
        
        return body.toString();
    }

    /**
     * Get email body for interview reminder
     */
    private String getInterviewReminderEmailBody(RAInterview interview) {
        StringBuilder body = new StringBuilder();
        body.append("This is a reminder that you have an interview scheduled:\n\n");
        body.append("RA Position: ").append(interview.getRaJob().getTitle()).append("\n");
        body.append("Date & Time: ").append(DATETIME_FORMAT.format(interview.getInterviewTime())).append("\n");
        
        if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
            body.append("Location: ").append(interview.getLocation()).append("\n");
        }
        if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
            body.append("Meeting Link: ").append(interview.getMeetingLink()).append("\n");
        }
        
        body.append("\nPlease make sure you are prepared and available at the scheduled time.\n\n");
        body.append("Best regards,\n");
        body.append("CSHub System");
        
        return body.toString();
    }

    /**
     * Save notification record to database
     */
    private void saveNotification(RAInterview interview, String notificationType, Long sentToId, String subject, String body) {
        try {
            InterviewNotification notification = new InterviewNotification();
            notification.setInterview(interview);
            notification.setNotificationType(notificationType);
            notification.setSentTo(models.User.find.byId(sentToId));
            notification.setSentTime(new Date());
            notification.setEmailSubject(subject);
            notification.setEmailBody(body);
            notification.save();
        } catch (Exception e) {
            Logger.error("InterviewEmailService.saveNotification: Failed to save notification", e);
        }
    }
}

