package model;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

public class Timetable {
    private String studentEmail;
    private Map<String, List<TimeSlot>> courseSlots;
    private Map<String, Map<Integer, String>> chosenActivities;

    /**
     * Constructs a new Timetable for a student.
     * 
     * @param studentEmail the email of the student
     */
    public Timetable(String studentEmail) {
        this.studentEmail = studentEmail;
        this.courseSlots = new HashMap<>();
        this.chosenActivities = new HashMap<>();
    }

    /**
     * Adds time slots for a course.
     * 
     * @param courseCode the course code
     * @param activities the activities to add
     * @return the number of slots added
     */
    public int addTimeSlots(String courseCode, String activities) {
        // Implementation would parse activities string and add time slots
        // For now returning a placeholder value
        return 0;
    }

    /**
     * Returns the number of activities chosen for a course.
     * 
     * @param courseCode the course code
     * @return the count of chosen activities
     */
    public int chosenActivities(int courseCode) {
        String code = String.valueOf(courseCode);
        return chosenActivities.containsKey(code) ? chosenActivities.get(code).size() : 0;
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
        // Implementation would check for time conflicts
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
        chosenActivities.computeIfAbsent(courseCode, k -> new HashMap<>())
                        .put(activityId, status);
    }

    /**
     * Checks if the timetable has slots for a given course.
     * 
     * @param courseCode the course code
     * @return true if slots exist for the course
     */
    public boolean hasSlotsForCourse(String courseCode) {
        return courseSlots.containsKey(courseCode) && !courseSlots.get(courseCode).isEmpty();
    }

    /**
     * Removes all slots for a given course.
     * 
     * @param courseCode the course code
     */
    public void removeSlotsForCourse(String courseCode) {
        courseSlots.remove(courseCode);
        chosenActivities.remove(courseCode);
    }

    @Override
    public String toString() {
        return "Timetable for student: " + studentEmail;
    }
}
