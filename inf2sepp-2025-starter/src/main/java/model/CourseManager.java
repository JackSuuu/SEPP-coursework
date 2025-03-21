package model;

import java.util.ArrayList;
import java.util.List;

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
    int requiredTutorials, int requiredLabs) {
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
        
        // add the course into the array
        getCourseArray().add(course);
        
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

    public boolean addCourseToStudentTimetable(String studentEmail, String courseCode) {
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

    // !BUG
    public boolean chooseActivityForCourse(String studentEmail, String courseCode, int activityId, String status) {
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
        
        // Find the specific activity in the course using activityId.
        List<Activity> activities = targetCourse.getActivities();
        Activity selectedActivity = null;
        for (Activity activity : activities) {
            if (activity.getId() == activityId) {
                // Set the status using the provided parameter.
                activity.setStatus(status);
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
            int slotsAdded = studentTimetable.addTimeSlots(courseCode, java.util.Collections.singletonList(selectedActivity));
            // Successful addition if at least one slot was added.
            return slotsAdded > 0;
        }
    }

    public String viewCourses() {
        ArrayList<String> result = new ArrayList<>();;

        for (Course course : getCourseArray()) {
            result.add(course.toString());
        }
        if (result.isEmpty()) {
            return "\nNot course available in the system now\n";
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

    private boolean checkChosenActivities(String courseCode, Timetable timetable) {
        // Check if any activity in the course has already been chosen.
        // We'll locate the course from the system's course array and inspect its activities.
        for (Course course : getCourseArray()) {
            if (course.getCourseCode().equals(courseCode)) {
                for (Activity activity : course.getActivities()) {
                    if ("CHOSEN".equals(activity.getStatus())) {
                        return true;
                    }
                }
            }
        }
        return false;
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
