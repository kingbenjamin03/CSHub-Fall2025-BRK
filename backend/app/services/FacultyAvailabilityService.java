package services;

import models.FacultyAvailability;
import models.RAInterview;
import models.User;
import play.Logger;

import javax.inject.Singleton;
import java.util.Date;
import java.util.List;

@Singleton
public class FacultyAvailabilityService {

    /**
     * Get all availability slots for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @return list of availability slots
     */
    public List<FacultyAvailability> getAvailabilityByFacultyId(Long facultyId) {
        return FacultyAvailability.find.query()
                .where()
                .eq("faculty.id", facultyId)
                .eq("isAvailable", true)
                .orderBy("startTime asc")
                .findList();
    }

    /**
     * Get available slots for a faculty member within a date range
     *
     * @param facultyId the faculty member's user ID
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of available slots
     */
    public List<FacultyAvailability> getAvailableSlots(Long facultyId, Date startDate, Date endDate) {
        return FacultyAvailability.find.query()
                .where()
                .eq("faculty.id", facultyId)
                .eq("isAvailable", true)
                .ge("startTime", startDate)
                .le("endTime", endDate)
                .orderBy("startTime asc")
                .findList();
    }

    /**
     * Add a new availability slot for a faculty member
     *
     * @param facultyId the faculty member's user ID
     * @param startTime start time of the slot
     * @param endTime end time of the slot
     * @return the created availability slot
     */
    public FacultyAvailability addAvailabilitySlot(Long facultyId, Date startTime, Date endTime) {
        User faculty = User.find.byId(facultyId);
        if (faculty == null) {
            Logger.error("FacultyAvailabilityService.addAvailabilitySlot: Faculty not found with ID: " + facultyId);
            return null;
        }

        if (startTime.after(endTime)) {
            Logger.error("FacultyAvailabilityService.addAvailabilitySlot: Start time must be before end time");
            return null;
        }

        FacultyAvailability availability = new FacultyAvailability();
        availability.setFaculty(faculty);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.setAvailable(true);
        availability.save();

        return availability;
    }

    /**
     * Update an existing availability slot
     *
     * @param slotId the availability slot ID
     * @param startTime new start time
     * @param endTime new end time
     * @return the updated availability slot, or null if not found
     */
    public FacultyAvailability updateAvailabilitySlot(Long slotId, Date startTime, Date endTime) {
        FacultyAvailability availability = FacultyAvailability.find.byId(slotId);
        if (availability == null) {
            Logger.error("FacultyAvailabilityService.updateAvailabilitySlot: Slot not found with ID: " + slotId);
            return null;
        }

        if (startTime.after(endTime)) {
            Logger.error("FacultyAvailabilityService.updateAvailabilitySlot: Start time must be before end time");
            return null;
        }

        // Check if the updated slot conflicts with existing interviews
        if (checkSlotConflict(availability.getFaculty().getId(), startTime, endTime, slotId)) {
            Logger.error("FacultyAvailabilityService.updateAvailabilitySlot: Updated slot conflicts with existing interview");
            return null;
        }

        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availability.save();

        return availability;
    }

    /**
     * Delete an availability slot
     *
     * @param slotId the availability slot ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteAvailabilitySlot(Long slotId) {
        FacultyAvailability availability = FacultyAvailability.find.byId(slotId);
        if (availability == null) {
            Logger.error("FacultyAvailabilityService.deleteAvailabilitySlot: Slot not found with ID: " + slotId);
            return false;
        }

        // Check if the slot is used by any interviews
        List<RAInterview> interviews = RAInterview.find.query()
                .where()
                .eq("faculty.id", availability.getFaculty().getId())
                .ge("interviewTime", availability.getStartTime())
                .le("interviewTime", availability.getEndTime())
                .in("status", "pending", "confirmed")
                .findList();

        if (!interviews.isEmpty()) {
            Logger.error("FacultyAvailabilityService.deleteAvailabilitySlot: Cannot delete slot with scheduled interviews");
            return false;
        }

        availability.delete();
        return true;
    }

    /**
     * Check if a time slot conflicts with existing interviews
     *
     * @param facultyId the faculty member's user ID
     * @param startTime start time to check
     * @param endTime end time to check
     * @return true if there is a conflict, false otherwise
     */
    public boolean checkSlotConflict(Long facultyId, Date startTime, Date endTime) {
        return checkSlotConflict(facultyId, startTime, endTime, null);
    }

    /**
     * Check if a time slot conflicts with existing interviews (excluding a specific slot)
     *
     * @param facultyId the faculty member's user ID
     * @param startTime start time to check
     * @param endTime end time to check
     * @param excludeSlotId availability slot ID to exclude from conflict check (for updates)
     * @return true if there is a conflict, false otherwise
     */
    public boolean checkSlotConflict(Long facultyId, Date startTime, Date endTime, Long excludeSlotId) {
        // Check for overlapping interviews
        List<RAInterview> conflictingInterviews = RAInterview.find.query()
                .where()
                .eq("faculty.id", facultyId)
                .in("status", "pending", "confirmed")
                .or()
                .and()
                .ge("interviewTime", startTime)
                .le("interviewTime", endTime)
                .endAnd()
                .and()
                .le("interviewTime", startTime)
                .ge("interviewTime", endTime)
                .endAnd()
                .endOr()
                .findList();

        if (!conflictingInterviews.isEmpty()) {
            return true;
        }

        // Check for overlapping availability slots (if excludeSlotId is provided, exclude it)
        if (excludeSlotId != null) {
            List<FacultyAvailability> conflictingSlots = FacultyAvailability.find.query()
                    .where()
                    .eq("faculty.id", facultyId)
                    .eq("isAvailable", true)
                    .ne("id", excludeSlotId)
                    .or()
                    .and()
                    .ge("startTime", startTime)
                    .le("startTime", endTime)
                    .endAnd()
                    .and()
                    .le("startTime", startTime)
                    .ge("endTime", startTime)
                    .endAnd()
                    .endOr()
                    .findList();

            return !conflictingSlots.isEmpty();
        }

        return false;
    }
}

