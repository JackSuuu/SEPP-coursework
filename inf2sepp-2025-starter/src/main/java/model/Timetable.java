package model;

import java.util.ArrayList;
import java.util.List;

import model.TimeSlot.Statuses;

import java.io.ObjectInputFilter.Status;
import java.time.LocalDate;
import java.time.LocalTime;

public class Timetable {
    private String studentEmail;
    // * Place to store all time slots
    private ArrayList<TimeSlot> timeSlotsArrayList;
    private ArrayList<Course> timetableCoursesArrayList;

    /**
     * Constructs a new Timetable for a student.
     * 
     * @param studentEmail the email of the student
     */
    public Timetable(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public ArrayList<TimeSlot> getTimeSlotsArray() {
        return timeSlotsArrayList;
    }
    

    /**
     * Adds a course to the timetable.
     * 
     * @param course the course to add
     */
    public void addCourse(Course course) {
        if (timetableCoursesArrayList == null) {
            timetableCoursesArrayList = new ArrayList<>();
        }
        timetableCoursesArrayList.add(course);
    }

    /**
     * Removes a course from the timetable.
     *
     * @param course the course to remove
     */
    public void removeCourse(Course course) {
        if (timetableCoursesArrayList != null) {
            timetableCoursesArrayList.remove(course);
        }
    }

    /**
     * Adds time slots for a course. This method is supporting the add activities to student timetable
     * 
     * @param courseCode the course code
     * @param activities the activities to add
     * @return the number of slots added
     */
    // * use Activity as an object instead of string to ensure parameter passing
    public String addTimeSlots(String courseCode, List<Activity> activities, Statuses status) {
        // Check for conflicts using hasSlotsForCourse before adding new time slots.
        int activityId = hasSlotsForCourse(courseCode, activities);
        if (hasSlotsForCourse(courseCode, activities) != 0) {
            // A conflict exists, so do not add new time slots.
            return courseCode + " " + activityId;
        }
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
                status
            );
            timeSlotsArrayList.add(slot);
            slotsAdded++;
        }
        return null;
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

    public boolean chooseActivity(String courseCode, int activityId) {
        if (timeSlotsArrayList == null) {
            //TODO assign
            //TODO assign :p
            return true;
        }
        for (TimeSlot slot : timeSlotsArrayList) { // TODO HASHMAP HERE
            if (slot.getCourseCode().equals(courseCode) && slot.getActivityId() == activityId) {
                slot.setStatus(Statuses.CHOSEN);
                break;
            }
        }
        return false;    // Temporary? prevents errors when running
    }

    /**
     * Checks if the timetable has slots for a given course.
     * 
     * @param courseCode the course code
     * @return true if slots exist for the course
     */
    public int hasSlotsForCourse(String courseCode, List<Activity> activities) {
        if (timeSlotsArrayList == null) {
            return 0;
        }
        // For each activity provided, check if adding it would conflict with existing slots for the course.
        for (Activity activity : activities) {
            for (TimeSlot slot : timeSlotsArrayList) {
                if (slot.getCourseCode().equals(courseCode)) {
                    if (checkConflicts(activity.getStartDate(), activity.getStartTime(), activity.getEndDate(), activity.getEndTime()) == 1) {
                        return activity.getId();
                    }
                }
            }
        }
        return 0;
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
        sb.append("Timetable for student with Email: ").append(studentEmail).append("\n");
        if (timeSlotsArrayList != null) {
            for (TimeSlot slot : timeSlotsArrayList) {
            sb.append(slot.toString());
            }
        }
        return sb.toString();
    }
}
