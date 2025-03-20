package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class Timetable {
    private String studentEmail;
    private ArrayList<TimeSlot> timeSlotsArrayList;

    /**
     * Constructs a new Timetable for a student.
     * 
     * @param studentEmail the email of the student
     */
    public Timetable(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    /**
     * Adds time slots for a course.
     * 
     * @param courseCode the course code
     * @param activities the activities to add
     * @return the number of slots added
     */
    // ! use Activity as an object instead of string to ensure parameter passing
    public int addTimeSlots(String courseCode, List<Activity> activities) {
        if (timeSlotsArrayList == null) {
            timeSlotsArrayList = new ArrayList<>();
        }
        int slotsAdded = 0;
        for (Activity activity : activities) {
            TimeSlot slot = new TimeSlot(
                activity.getDay(),
                activity.getStartTime(),
                activity.getEndTime(),
                courseCode,
                activity.getId(),
                activity.getStatus().toString()
            );
            timeSlotsArrayList.add(slot);
            slotsAdded++;
        }
        return slotsAdded;
    }

    /**
     * Returns the number of activities chosen for a course.
     * 
     * @param courseCode the course code
     * @return the count of chosen activities
     */
    public int chosenActivities(int courseCode) {
        if (timeSlotsArrayList == null) {
            return 0;
        }
        int count = 0;
        for (TimeSlot slot : timeSlotsArrayList) {
            if (slot.isChosen()) {
            count++;
            }
        }
        return count;
    }

    /**
     * Checks for time conflicts with existing slots.
     * 
     * @param startDate the start date
     * @param startTime the start time
     * @param endDate the end date
     * @param endTime the end time
     * @return 0 if no conflict, otherwise a code indicating the conflict
     */
    @SuppressWarnings("unused")
    private int checkConflicts(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        if (timeSlotsArrayList == null) {
            return 0;
        }
        
        // Create input time interval as LocalDateTime instances
        java.time.LocalDateTime inputStart = java.time.LocalDateTime.of(startDate, startTime);
        java.time.LocalDateTime inputEnd = java.time.LocalDateTime.of(endDate, endTime);
        
        for (TimeSlot slot : timeSlotsArrayList) {
            // Assuming that slot.getDay() returns a LocalDate,
            // and slot.getStartTime() and slot.getEndTime() return LocalTime.
            java.time.LocalDateTime slotStart = java.time.LocalDateTime.of(
                java.time.LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(slot.getDay())),
                slot.getStartTime());
            java.time.LocalDateTime slotEnd = java.time.LocalDateTime.of(
                java.time.LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(slot.getDay())),
                slot.getEndTime());
            
            // Check for any overlap between input interval and the slot's interval.
            if (inputStart.isBefore(slotEnd) && inputEnd.isAfter(slotStart)) {
                // Conflict found; you can define various codes as needed. Here we return 1.
                return 1;
            }
        }
        return 0;
    }

    /**
     * Checks if the timetable has a student email assigned.
     * 
     * @return true if student email is not null or empty
     */
    public boolean hasStudentEmail() {
        return studentEmail != null && !studentEmail.isEmpty();
    }

    /**
     * Sets the status of an activity for a course.
     * 
     * @param courseCode the course code
     * @param activityId the ID of the activity
     * @param status the status to set
     */
    public void choseActivity(String courseCode, int activityId, String status) {
        if (timeSlotsArrayList == null) {
            return;
        }
        for (TimeSlot slot : timeSlotsArrayList) {
            if (slot.getCourseCode().equals(courseCode) && slot.getActivityId() == activityId) {
                slot.setStatus(status);
                break;
            }
        }
    }

    /**
     * Checks if the timetable has slots for a given course.
     * 
     * @param courseCode the course code
     * @return true if slots exist for the course
     */
    // ! should have problem, need to fix
    public boolean hasSlotsForCourse(String courseCode) {
        if (timeSlotsArrayList == null) {
            return false;
        }
        // For each slot matching the course code, use checkConflicts on its time interval.
        // The idea is that if a slot exists (i.e. causes a conflict when compared against the timetable),
        // it means the timetable already has slots for the course.
        for (TimeSlot slot : timeSlotsArrayList) {
            if (slot.getCourseCode().equals(courseCode)) {
                java.time.LocalDate date = java.time.LocalDate.now()
                        .with(java.time.temporal.TemporalAdjusters.nextOrSame(slot.getDay()));
                if (checkConflicts(date, slot.getStartTime(), date, slot.getEndTime()) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes all slots for a given course.
     * 
     * @param courseCode the course code
     */
    public void removeSlotsForCourse(String courseCode) {
        if (timeSlotsArrayList != null) {
            timeSlotsArrayList.removeIf(slot -> slot.getCourseCode().equals(courseCode));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Timetable for student: ").append(studentEmail).append("\n");
        if (timeSlotsArrayList != null) {
            for (TimeSlot slot : timeSlotsArrayList) {
            sb.append(slot.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
