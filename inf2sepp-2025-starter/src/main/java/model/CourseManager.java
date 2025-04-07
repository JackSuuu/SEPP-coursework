package model;

import java.util.ArrayList;
import java.util.List;
import model.*;
import view.View;

public class CourseManager {
    // private Timetable Timetable = new Timetable();
    private final ArrayList<Course> courseArray = new ArrayList<>();
    private final ArrayList<Timetable> timetablesArray = new ArrayList<>();
    
    public ArrayList<Timetable> getTimetableArray() {
        return timetablesArray;
    }

    // To support store all added course and remove
    public ArrayList<Course> getCourseArray() {
        return courseArray;
    }

    public void addCourse(String courseCode, String name, String description, boolean requiresComputers,
    String CourseOrganiserName, String CourseOrganiserEmail,
    String CourseSecretaryName, String CourseSecretaryEmail,
    int requiredTutorials, int requiredLabs, View view) {
        Course course = new Course(courseCode, name, description, requiresComputers,
        CourseOrganiserName, CourseOrganiserEmail,
        CourseSecretaryName, CourseSecretaryEmail,
        requiredTutorials, requiredLabs);
        course.setCourseCode(courseCode);
        course.setCourseName(name);
        course.setCourseDescription(description);
        course.setRequiresComputers(requiresComputers);
        course.setCourseOrganiserName(CourseOrganiserName);
        course.setCourseOrganiserEmail(CourseOrganiserEmail);
        course.setCourseSecretaryEmail(CourseSecretaryEmail);
        course.setRequiredTutorials(requiredTutorials);
        course.setRequiredLabs(requiredLabs);

        String courseInfo = "Code: " + courseCode +
        ", Name: " + name +
        ", Description: " + description +
        ", Needs Computer: " + requiresComputers +
        ", Organiser Name: " + CourseOrganiserName +
        ", Organiser Email: " + CourseOrganiserEmail +
        ", Secretary Name: " + CourseOrganiserName +
        ", Secretary Email: " + CourseSecretaryEmail +
        ", Tutorials: " + requiredTutorials +
        ", Labs: " + requiredLabs;

        if (!courseCodeValid(courseCode)) {
            KioskLogger.getInstance().log(CourseOrganiserEmail, "addCourse", courseInfo, "FAILURE (Error: Provided courseCode is invalid)");
            view.displayError("Provided courseCode is invalid");
            // the process halt, user should add course again
            return;
        } 

        if (hasCode(courseCode)) {
            KioskLogger.getInstance().log(CourseOrganiserEmail, "addCourse", courseInfo, "FAILURE (Error: Course with that code already exists)");
            view.displayError("Course with that code already exists.");
            return;
        } else {
            getCourseArray().add(course);
        }        
    }

    public boolean courseCodeValid(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    // align with sequence diagram
    public boolean hasCode(String courseCode) {
        // Detect if the course code is valid
        boolean duplicateFound = false;
        for (Course existingCourse : getCourseArray()) {
            if (existingCourse.getCourseCode().equals(courseCode)) {
            duplicateFound = true;
            break;
            }
        }
        if (duplicateFound) {
            return true;
        } else {
            return false;
        }
    }

    // * modify this method to boolean, so we can testify if the course been add successfully
    public boolean removeCourse(String courseCode) {
        Course delete_course = null;
        for (Course course: getCourseArray()) {
            if (course.getCourseCode().equals(courseCode)) {
                delete_course = course;
                break;
            }
        };
        if (delete_course == null) {
            System.out.println(courseCode + " has error, course not found");
            return false;
        } else {
            getCourseArray().remove(delete_course);
            return true;
        }
    }

    public boolean addCourseToStudentTimetable(String studentEmail, String courseCode, View view) {
        if (!hasCode(courseCode)) {
            view.displayError("Incorrect course code");
            KioskLogger.getInstance().log(studentEmail, "addCoursetoStudentTimetable", studentEmail + courseCode, "FAILURE (Error: Incorrect course code provided)");
            return false;
        }

        // Iterate through student time to find the target course
        Course targetCourse = null;
        for (Course c : getCourseArray()) {
            if (c.getCourseCode().equals(courseCode)) {
            targetCourse = c;
            break;
            }
        }
        if (targetCourse == null) {
            System.out.println("Course " + courseCode + " not found\n");
            return false;
        }
        
        // Check if a Timetable for the student already exists
        Timetable studentTimetable = null;
        for (Timetable t : getTimetableArray()) {
            if (t.getStudentEmail().equals(studentEmail)) {
            studentTimetable = t;
            break;
            }
        }
        
        // Equivalent code for hasEmail
        // If no timetable exists, create a new one with the studentEmail
        if (studentTimetable == null) {
            studentTimetable = new Timetable(studentEmail);
            getTimetableArray().add(studentTimetable);
        }
        
        // Append the course into the student's timetable
        studentTimetable.addCourse(targetCourse);
        return true;
    }

    public boolean removeCourseFromStudentTimetable(String studentEmail, String courseCode) {
        // Locate the student's timetable
        Timetable studentTimetable = null;
        for (Timetable t : getTimetableArray()) {
            if (t.getStudentEmail().equals(studentEmail)) {
                studentTimetable = t;
                break;
            }
        }
        if (studentTimetable == null) {
            System.out.println("Timetable for student " + studentEmail + " not found\n");
            return false;
        }

        // Find the course in the student's timetable
        Course targetCourse = null;
        for (Course c : getCourseArray()) {
            if (c.getCourseCode().equals(courseCode)) {
                targetCourse = c;
                break;
            }
        }
        if (targetCourse == null) {
            System.out.println("Course " + courseCode + " not found in student's timetable\n");
            return false;
        }

        // Remove the course from the student's timetable
        studentTimetable.removeCourse(targetCourse);
        return true;
    }

    public boolean chooseActivityForCourse(String studentEmail, String courseCode, int activityId, String status, View view) {
        // Convert status string to ENUM
        TimeSlot.Statuses statusEnum;
        try {
            statusEnum = TimeSlot.Statuses.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status provided: " + status);
            return false;
        }

        // Find the student's timetable, or create one if not found
        Timetable studentTimetable = null;
        for (Timetable t : getTimetableArray()) {
            if (t.getStudentEmail().equals(studentEmail)) {
                studentTimetable = t;
                break;
            }
        }
        if (studentTimetable == null) {
            studentTimetable = new Timetable(studentEmail);
            getTimetableArray().add(studentTimetable);
        }
        
        // Find the target course by courseCode
        Course targetCourse = null;
        for (Course course : getCourseArray()) {
            if (course.getCourseCode().equals(courseCode)) {
                targetCourse = course;
                break;
            }
        }
        if (targetCourse == null) {
            System.out.println("Course " + courseCode + " not found\n");
            return false;
        }
        
        // Use checkChosenTutorials or checkChosenLabs based on the activityId before selecting the activity.
        if (activityId == 2 && !checkChosenTutorials(courseCode, studentTimetable)) {
            KioskLogger.getInstance().log(studentEmail, "addCoursetoStudentTimetable", studentEmail+courseCode, "FAILURE"+ "(Warning: number of required tutorials " + targetCourse.getRequiredTutorials() + "not yet chosen)");
            view.displayWarning("You have to choose " + targetCourse.getRequiredTutorials() + "tutorials for this course");
            return false;
        }
        if (activityId == 3 && !checkChosenLabs(courseCode, studentTimetable)) {
            KioskLogger.getInstance().log(studentEmail, "addCoursetoStudentTimetable", studentEmail+courseCode, "FAILURE"+ "(Warning: number of required labs " + targetCourse.getRequiredLabs() + "not yet chosen)");
            view.displayWarning("You have to choose " + targetCourse.getRequiredLabs() + "labs for this course");
            return false;
        }
        
        // Find the specific activity in the course using activityId.
        List<Activity> activities = targetCourse.getActivities();
        Activity selectedActivity = null;
        for (Activity activity : activities) {
            if (activity.getId() == activityId) {
                selectedActivity = activity;
                break;
            }
        }
        if (selectedActivity == null) {
            System.out.println("Activity " + activityId + " not found in course " + courseCode + "\n");
            return false;
        } else {
            // Use addTimeSlots to add the activity into the student's timetable.
            // The method expects a list, so we wrap the selected activity.
            String slotsAdded = studentTimetable.addTimeSlots(courseCode, java.util.Collections.singletonList(selectedActivity), statusEnum);
            // Successful addition if at least one slot was added.
            if (slotsAdded != null) {
                return true;
            } else {
                KioskLogger.getInstance().log(studentEmail, "addCoursetoStudentTimetable", studentEmail + courseCode, "FAILURE (Warning: at least one clash with another activity)");
                return false;
            }
        }
    }

    public String viewCourses() {
        ArrayList<String> result = new ArrayList<>();;

        for (Course course : getCourseArray()) {
            result.add(course.toString());
        }
        if (result.isEmpty()) {
            return "\nNo courses are available in the system right now.\n";
        } else {
            return result.toString();
        }
    }

    public String viewCourse(String code) {
        Course selectedCourse = null;

        for (Course course : getCourseArray()) {
            if (course.getCourseCode().equals(code)) {
                selectedCourse = course;
            }
        }

        if (selectedCourse == null) {
            return "Course not found\n";
        }
        
        return selectedCourse.toString();
    }

    private boolean checkChosenTutorials(String courseCode, Timetable timetable) {
        int chosenTutorials = 0;
        ArrayList<TimeSlot> timeSlots = timetable.getTimeSlotsArray();
        for (TimeSlot slot : timeSlots) {
            if (slot.getCourseCode().equals(courseCode) && slot.getActivityId() == 2 && slot.isChosen()) {
                chosenTutorials++;
            }
        }
        // Retrieve the Course to check required number of tutorials
        Course targetCourse = null;
        for (Course course : getCourseArray()) {
            if (course.getCourseCode().equals(courseCode)) {
                targetCourse = course;
                break;
            }
        }
        if (targetCourse == null) {
            // Course not found; assume tutorials are not enough
            return false;
        }
        // Return true if the number of chosen tutorials is greater or equal to required
        return chosenTutorials >= targetCourse.getRequiredTutorials();
    }
    
    private boolean checkChosenLabs(String courseCode, Timetable timetable) {
        int chosenLabs = 0;
        ArrayList<TimeSlot> timeSlots = timetable.getTimeSlotsArray();

        for (TimeSlot slot : timeSlots) {
            if (slot.getCourseCode().equals(courseCode) && slot.getActivityId() == 3 && slot.isChosen()) {
                chosenLabs++;
            }
        }
        // Retrieve the Course to check required number of labs
        Course targetCourse = null;
        for (Course course : getCourseArray()) {
            if (course.getCourseCode().equals(courseCode)) {
                targetCourse = course;
                break;
            }
        }
        if (targetCourse == null) {
            return false;
        }
        // Return true if the number of chosen labs is greater or equal to required labs
        return chosenLabs >= targetCourse.getRequiredLabs();
    }

    private Timetable getTimetable(String studentEmail) {
    for (Timetable t : getTimetableArray()) {
        if (t.getStudentEmail().equals(studentEmail)) {
            return t;
        }
    }
    return null;
    }

    // View timetable by using student Email
    public Timetable viewTimetable(String studentEmail) {
        Timetable timetable = getTimetable(studentEmail);
        if (timetable == null) {
            return null;
        }
        return timetable;
    }
}
