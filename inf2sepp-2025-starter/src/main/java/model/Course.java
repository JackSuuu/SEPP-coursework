package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Course {
    public enum ActivityType {
        LAB,
        TUTORIAL,
        LECTURE
    }

    private String courseCode;
    private String name;
    private String description;
    private boolean requiresComputers;
    private String courseOrganiserName;
    private String courseOrganiserEmail;
    private String courseSecretaryName;
    private String courseSecretaryEmail;
    private int requiredTutorials;
    private int requiredLabs;

    public Course(String courseCode, String name, String description, boolean requiresComputers,
                  String courseOrganiserName, String courseOrganiserEmail,
                  String courseSecretaryName, String courseSecretaryEmail,
                  int requiredTutorials, int requiredLabs) {
        this.courseCode = courseCode;
        this.name = name;
        this.description = description;
        this.requiresComputers = requiresComputers;
        this.courseOrganiserName = courseOrganiserName;
        this.courseOrganiserEmail = courseOrganiserEmail;
        this.courseSecretaryName = courseSecretaryName;
        this.courseSecretaryEmail = courseSecretaryEmail;
        this.requiredTutorials = requiredTutorials;
        this.requiredLabs = requiredLabs;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setCourseName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setCourseDescription(String description) {
        this.description = description;
    }

    public boolean isRequiresComputers() {
        return requiresComputers;
    }

    public void setRequiresComputers(boolean requiresComputers) {
        this.requiresComputers = requiresComputers;
    }

    public String getCourseOrganiserName() {
        return courseOrganiserName;
    }

    public void setCourseOrganiserName(String courseOrganiserName) {
        this.courseOrganiserName = courseOrganiserName;
    }

    public String getCourseOrganiserEmail() {
        return courseOrganiserEmail;
    }

    public void setCourseOrganiserEmail(String courseOrganiserEmail) {
        this.courseOrganiserEmail = courseOrganiserEmail;
    }

    public String getCourseSecretaryName() {
        return courseSecretaryName;
    }

    public void setCourseSecretaryName(String courseSecretaryName) {
        this.courseSecretaryName = courseSecretaryName;
    }

    public String getCourseSecretaryEmail() {
        return courseSecretaryEmail;
    }

    public void setCourseSecretaryEmail(String courseSecretaryEmail) {
        this.courseSecretaryEmail = courseSecretaryEmail;
    }

    public int getRequiredTutorials() {
        return requiredTutorials;
    }

    public void setRequiredTutorials(int requiredTutorials) {
        this.requiredTutorials = requiredTutorials;
    }

    public int getRequiredLabs() {
        return requiredLabs;
    }

    public void setRequiredLabs(int requiredLabs) {
        this.requiredLabs = requiredLabs;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    private List<Activity> activities = new ArrayList<>();

    public void addActivity(int id, ActivityType type, LocalDate startDate, LocalTime startTime, LocalDate endDate, 
                            LocalTime endTime, String location, DayOfWeek day, int capacity) {
        Activity activity;
        switch (type) {
            case LAB:
                activity = new Lab(id, startDate, startTime, endDate, endTime, location, day, capacity);
                break;
            case TUTORIAL:
                activity = new Tutorial(id, startDate, startTime, endDate, endTime, location, day, capacity);
                break;
            case LECTURE:
                activity = new Lecture(id, startDate, startTime, endDate, endTime, location, day, capacity);
                break;
            default:
                throw new IllegalArgumentException("Unknown activity type: " + type);
        }
        activities.add(activity);
    }
    
    public void removeActivities() {
        activities.clear();
    }

    public boolean hasCode(String code) {
        return this.courseCode.equals(code);
    }

    @Override
    public String toString() {
        return "Course{" +
               "courseCode='" + courseCode + '\'' +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", requiresComputers=" + requiresComputers +
               ", courseOrganiserName='" + courseOrganiserName + '\'' +
               ", courseOrganiserEmail='" + courseOrganiserEmail + '\'' +
               ", courseSecretaryName='" + courseSecretaryName + '\'' +
               ", courseSecretaryEmail='" + courseSecretaryEmail + '\'' +
               ", requiredTutorials=" + requiredTutorials +
               ", requiredLabs=" + requiredLabs +
               '}';
    }
}
