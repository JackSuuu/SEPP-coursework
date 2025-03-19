package model;

import java.util.ArrayList;

public class CourseManager extends SharedContext {
    // private Timetable Timetable = new Timetable();
    private ArrayList<Course> courseArray = new ArrayList<>();
    
    // public Timetable getTimetable() {
    //     return Timetable;
    // }

    // ! To support store all added course and remove
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

    public void removeCourse(String courseCode) {
        Course delete_course = null;
        for (Course course: getCourseArray()) {
            if (course.getCourseCode() == courseCode) {
                delete_course = course;
            }
        };
        if (delete_course == null) {
            System.out.println(courseCode + "has error, course not found");
        } else {
            getCourseArray().remove(delete_course);
        }
    }

    // public boolean addCourseToStudentTimetable(String studentEmail, String courseCode) {
    //     Course course = getTimetable().getCourse(courseCode);
    //     if (course == null) {
    //         return false;
    //     }
        
    //     Timetable timetable = getTimetable(studentEmail);
    //     if (timetable == null) {
    //         timetable = new Timetable();
    //         getStudentTable().getStudent(studentEmail).setTimetable(timetable);
    //     }
        
    //     timetable.addCourse(course);
    //     return true;
    // }

    // public boolean chooseActivityForCourse(String studentEmail, String courseCode, int activityId, String status) {
    //     Timetable timetable = getTimetable(studentEmail);
    //     if (timetable == null) {
    //         return false;
    //     }
        
    //     return timetable.setActivityStatus(courseCode, activityId, status);
    // }

    public String viewCourses() {
        ArrayList<String> result = new ArrayList<>();;

        for (Course course : getCourseArray()) {
            result.add(course.toString());
        }
        return result.toString();
    }

    public String viewCourse(String code) {
        Course selectedCourse = null;

        for (Course course : getCourseArray()) {
            if (course.getCourseCode() == code) {
                selectedCourse = course;
            }
        }

        if (selectedCourse == null) {
            return "Course not found";
        }
        
        return String.format("%s: %s - %s", 
                            selectedCourse.toString());
    }

    // private boolean checkChosenActivities(String courseCode, Timetable timetable) {
    //     Course course = getTimetable().getCourse(courseCode);
    //     if (course == null || timetable == null) {
    //         return false;
    //     }
        
    //     int tutorialCount = 0;
    //     int labCount = 0;
        
    //     for (Activity activity : timetable.getActivities()) {
    //         if (activity.getCourseCode().equals(courseCode)) {
    //             if (activity.getType().equalsIgnoreCase("tutorial") && 
    //                 activity.getStatus().equalsIgnoreCase("enrolled")) {
    //                 tutorialCount++;
    //             } else if (activity.getType().equalsIgnoreCase("lab") && 
    //                       activity.getStatus().equalsIgnoreCase("enrolled")) {
    //                 labCount++;
    //             }
    //         }
    //     }
        
    //     return tutorialCount >= course.getRequiredTutorials() && 
    //            labCount >= course.getRequiredLabs();
    // }

    // private Timetable getTimetable(String studentEmail) {
    //     Student student = getStudentTable().getStudent(studentEmail);
    //     if (student == null) {
    //         return null;
    //     }
    //     return student.getTimetable();
    // }

    // public String viewTimetable(String studentEmail) {
    //     Timetable timetable = getTimetable(studentEmail);
    //     if (timetable == null) {
    //         return "No timetable found for this student";
    //     }
        
    //     StringBuilder result = new StringBuilder("Timetable for " + studentEmail + ":\n");
    //     for (Activity activity : timetable.getActivities()) {
    //         result.append(String.format("%s - %s (ID: %d) - %s\n", 
    //                                    activity.getCourseCode(), 
    //                                    activity.getType(), 
    //                                    activity.getActivityId(), 
    //                                    activity.getStatus()));
    //     }
        
    //     return result.toString();
    // }

}
