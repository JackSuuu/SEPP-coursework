package controller;

import external.AuthenticationService;
import external.EmailService;
import model.SharedContext;
import view.View;

public class StudentController extends Controller {
        public StudentController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }
    /**
     * Manages the student's timetable by providing options for various timetable operations.
     */
    public void manageTimetable() {
        boolean running = true;
        while (running) {
            String[] options = {"Add Course", "Remove Course", "View Timetable", "Choose Activity for Course", "Return to Main Menu"};
            int choice = view.showMenu("Timetable Management", options);
            
            switch (choice) {
                case 0: // Add Course
                    String courseToAdd = view.getString("Enter the course code to add:");
                    addCourse(courseToAdd);
                    break;
                case 1: // Remove Course
                    String courseToRemove = view.getString("Enter the course code to remove:");
                    removeCourse(courseToRemove);
                    break;
                case 2: // View Timetable
                    viewTimetable();
                    break;
                case 3: // Choose Activity
                    String courseCode = view.getString("Enter the course code:");
                    String activityId = view.getString("Enter the activity ID:");
                    chooseActivityForCourse(courseCode, activityId);
                    break;
                case 4: // Return
                    running = false;
                    break;
            }
        }
    }

    /**
     * Adds a course to the student's timetable.
     * 
     * @param courseCode The code of the course to add.
     */
    public void addCourse(String courseCode) {
        // Implement logic to add course to timetable
        if (courseCode != null && !courseCode.isEmpty()) {
            view.showMessage("Course " + courseCode + " has been added to your timetable.");
        } else {
            view.showMessage("Invalid course code. Please try again.");
        }
    }

    /**
     * Removes a course from the student's timetable.
     * 
     * @param courseCode The code of the course to remove.
     */
    public void removeCourse(String courseCode) {
        // Implement logic to remove course from timetable
        if (courseCode != null && !courseCode.isEmpty()) {
            view.showMessage("Course " + courseCode + " has been removed from your timetable.");
        } else {
            view.showMessage("Invalid course code. Please try again.");
        }
    }

    /**
     * Displays the student's current timetable.
     */
    public void viewTimetable() {
        // Retrieve and display the student's timetable
        view.showMessage("Displaying your current timetable...");
        // In a real implementation, you would show actual timetable data
    }

    /**
     * Selects a specific activity for a given course.
     * 
     * @param courseCode The code of the course.
     * @param activityId The ID of the activity to choose.
     */
    public void chooseActivityForCourse(String courseCode, String activityId) {
        // Implement logic to assign activity to course
        if (courseCode != null && !courseCode.isEmpty() && activityId != null && !activityId.isEmpty()) {
            view.showMessage("Activity " + activityId + " has been chosen for course " + courseCode + ".");
        } else {
            view.showMessage("Invalid course code or activity ID. Please try again.");
        }
    }
}
