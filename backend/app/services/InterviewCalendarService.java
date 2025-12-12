package services;

import models.RAInterview;
import play.Logger;

import javax.inject.Singleton;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Singleton
public class InterviewCalendarService {

    private static final SimpleDateFormat ICS_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final SimpleDateFormat ICS_DATE_FORMAT_UTC = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    static {
        ICS_DATE_FORMAT_UTC.setTimeZone(UTC);
    }

    /**
     * Generate iCal (.ics) file content for an interview
     *
     * @param interview the interview
     * @return iCal file content as string
     */
    public String generateICalEvent(RAInterview interview) {
        StringBuilder ics = new StringBuilder();
        
        // iCal header
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("PRODID:-//CSHub//Interview Scheduling//EN\n");
        ics.append("CALSCALE:GREGORIAN\n");
        ics.append("METHOD:REQUEST\n");
        
        // Event
        ics.append("BEGIN:VEVENT\n");
        
        // UID (unique identifier)
        ics.append("UID:interview-").append(interview.getId()).append("@cshub\n");
        
        // Date/time
        Date startTime = interview.getInterviewTime();
        Date endTime = new Date(startTime.getTime() + 3600000); // 1 hour duration
        
        ics.append("DTSTART:").append(ICS_DATE_FORMAT.format(startTime)).append("\n");
        ics.append("DTEND:").append(ICS_DATE_FORMAT.format(endTime)).append("\n");
        ics.append("DTSTAMP:").append(ICS_DATE_FORMAT_UTC.format(new Date())).append("\n");
        
        // Summary (title)
        String summary = "Interview: " + interview.getRaJob().getTitle();
        ics.append("SUMMARY:").append(escapeICalText(summary)).append("\n");
        
        // Description
        StringBuilder description = new StringBuilder();
        description.append("RA Position Interview\n\n");
        description.append("Position: ").append(interview.getRaJob().getTitle()).append("\n");
        description.append("Applicant: ").append(interview.getApplicant().getFirstName())
            .append(" ").append(interview.getApplicant().getLastName()).append("\n");
        description.append("Faculty: ").append(interview.getFaculty().getFirstName())
            .append(" ").append(interview.getFaculty().getLastName()).append("\n");
        
        if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
            description.append("Location: ").append(interview.getLocation()).append("\n");
        }
        if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
            description.append("Meeting Link: ").append(interview.getMeetingLink()).append("\n");
        }
        if (interview.getNotes() != null && !interview.getNotes().isEmpty()) {
            description.append("\nNotes: ").append(interview.getNotes()).append("\n");
        }
        
        ics.append("DESCRIPTION:").append(escapeICalText(description.toString())).append("\n");
        
        // Location
        if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
            ics.append("LOCATION:").append(escapeICalText(interview.getLocation())).append("\n");
        } else if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
            ics.append("LOCATION:").append(escapeICalText(interview.getMeetingLink())).append("\n");
        }
        
        // Organizer (faculty)
        String organizerEmail = interview.getFaculty().getEmail();
        String organizerName = interview.getFaculty().getFirstName() + " " + interview.getFaculty().getLastName();
        ics.append("ORGANIZER;CN=").append(escapeICalText(organizerName))
            .append(":MAILTO:").append(organizerEmail).append("\n");
        
        // Attendee (applicant)
        String attendeeEmail = interview.getApplicant().getEmail();
        String attendeeName = interview.getApplicant().getFirstName() + " " + interview.getApplicant().getLastName();
        ics.append("ATTENDEE;CN=").append(escapeICalText(attendeeName))
            .append(";RSVP=TRUE:MAILTO:").append(attendeeEmail).append("\n");
        
        // Status
        if ("confirmed".equals(interview.getStatus())) {
            ics.append("STATUS:CONFIRMED\n");
        } else {
            ics.append("STATUS:TENTATIVE\n");
        }
        
        // Priority
        ics.append("PRIORITY:5\n");
        
        // Reminder (15 minutes before)
        ics.append("BEGIN:VALARM\n");
        ics.append("TRIGGER:-PT15M\n");
        ics.append("ACTION:DISPLAY\n");
        ics.append("DESCRIPTION:Interview Reminder\n");
        ics.append("END:VALARM\n");
        
        // End event
        ics.append("END:VEVENT\n");
        
        // End calendar
        ics.append("END:VCALENDAR\n");
        
        return ics.toString();
    }

    /**
     * Generate Google Calendar link for an interview
     *
     * @param interview the interview
     * @return Google Calendar URL
     */
    public String generateGoogleCalendarLink(RAInterview interview) {
        try {
            Date startTime = interview.getInterviewTime();
            Date endTime = new Date(startTime.getTime() + 3600000); // 1 hour duration
            
            // Google Calendar uses ISO 8601 format
            SimpleDateFormat googleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String start = googleDateFormat.format(startTime);
            String end = googleDateFormat.format(endTime);
            
            String title = URLEncoder.encode("Interview: " + interview.getRaJob().getTitle(), "UTF-8");
            
            StringBuilder description = new StringBuilder();
            description.append("RA Position Interview\n\n");
            description.append("Position: ").append(interview.getRaJob().getTitle()).append("\n");
            description.append("Applicant: ").append(interview.getApplicant().getFirstName())
                .append(" ").append(interview.getApplicant().getLastName()).append("\n");
            description.append("Faculty: ").append(interview.getFaculty().getFirstName())
                .append(" ").append(interview.getFaculty().getLastName()).append("\n");
            
            if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
                description.append("Location: ").append(interview.getLocation()).append("\n");
            }
            if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
                description.append("Meeting Link: ").append(interview.getMeetingLink()).append("\n");
            }
            
            String desc = URLEncoder.encode(description.toString(), "UTF-8");
            String location = "";
            if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
                location = URLEncoder.encode(interview.getLocation(), "UTF-8");
            } else if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
                location = URLEncoder.encode(interview.getMeetingLink(), "UTF-8");
            }
            
            return String.format(
                "https://calendar.google.com/calendar/render?action=TEMPLATE&text=%s&dates=%s/%s&details=%s&location=%s",
                title, start, end, desc, location
            );
        } catch (Exception e) {
            Logger.error("InterviewCalendarService.generateGoogleCalendarLink: Failed to generate link", e);
            return null;
        }
    }

    /**
     * Generate Outlook Calendar link for an interview
     *
     * @param interview the interview
     * @return Outlook Calendar URL
     */
    public String generateOutlookCalendarLink(RAInterview interview) {
        try {
            Date startTime = interview.getInterviewTime();
            Date endTime = new Date(startTime.getTime() + 3600000); // 1 hour duration
            
            // Outlook uses ISO 8601 format
            SimpleDateFormat outlookDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String start = outlookDateFormat.format(startTime);
            String end = outlookDateFormat.format(endTime);
            
            String subject = URLEncoder.encode("Interview: " + interview.getRaJob().getTitle(), "UTF-8");
            
            StringBuilder body = new StringBuilder();
            body.append("RA Position Interview\n\n");
            body.append("Position: ").append(interview.getRaJob().getTitle()).append("\n");
            body.append("Applicant: ").append(interview.getApplicant().getFirstName())
                .append(" ").append(interview.getApplicant().getLastName()).append("\n");
            body.append("Faculty: ").append(interview.getFaculty().getFirstName())
                .append(" ").append(interview.getFaculty().getLastName()).append("\n");
            
            if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
                body.append("Location: ").append(interview.getLocation()).append("\n");
            }
            if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
                body.append("Meeting Link: ").append(interview.getMeetingLink()).append("\n");
            }
            
            String bodyText = URLEncoder.encode(body.toString(), "UTF-8");
            String location = "";
            if (interview.getLocation() != null && !interview.getLocation().isEmpty()) {
                location = URLEncoder.encode(interview.getLocation(), "UTF-8");
            } else if (interview.getMeetingLink() != null && !interview.getMeetingLink().isEmpty()) {
                location = URLEncoder.encode(interview.getMeetingLink(), "UTF-8");
            }
            
            return String.format(
                "https://outlook.live.com/calendar/0/deeplink/compose?subject=%s&startdt=%s&enddt=%s&body=%s&location=%s",
                subject, start, end, bodyText, location
            );
        } catch (Exception e) {
            Logger.error("InterviewCalendarService.generateOutlookCalendarLink: Failed to generate link", e);
            return null;
        }
    }

    /**
     * Escape text for iCal format
     * iCal requires special characters to be escaped
     */
    private String escapeICalText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace(",", "\\,")
                   .replace(";", "\\;")
                   .replace("\n", "\\n")
                   .replace("\r", "");
    }
}

