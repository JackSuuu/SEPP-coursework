package controller;

import external.AuthenticationService;
import external.EmailService;
import model.CourseManager;
import model.SharedContext;
import model.Timetable;
import model.AuthenticatedUser;
import view.View;

public class StudentController extends Controller {
        public StudentController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }
    /**
     * Manages the student's timetable by providing options for various timetable operations.
     */
    // TODO: implement this
    public void manageTimetable() {
        boolean running = true;
        while (running) {
            // get CourseManager and CurrentUser
            CourseManager manager = sharedContext.getCourseManager();
            AuthenticatedUser currentuser = (AuthenticatedUser) sharedContext.getCurrentUser();

            String[] options = {"Add Course", "Remove Course", "View Timetable", "Choose Activity for Course", "Return to Main Menu"};
            view.displayDivider();
            view.displayInfo("# Timetable Management - " + currentuser.getEmail() + '\n');
           
            for (int i = 0; i < options.length; i++) {
                view.displayInfo("[" + i + "] " + options[i]);
            }
            int choice = -1;
            try {
                view.displayDivider();
                choice = Integer.parseInt(view.getInput("Enter your choice: "));
            } catch (NumberFormatException e) {
                view.displayInfo("Invalid choice, please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 0: // Add Course to timetable
                    String courseToAdd = view.getInput("Enter the course code to add into your timetable: ");
                    Boolean addSuccess = manager.addCourseToStudentTimetable(currentuser.getEmail(), courseToAdd);
                    if (addSuccess) {
                        view.displaySuccess("Course " + courseToAdd + " has been add successfully");
                    } else {
                        view.displayError("Course " + courseToAdd + " add fail");
                    }

                    break;
                case 1: // Remove Course to timetable
                    String courseToRemove = view.getInput("Enter the course code to remove: ");
                    Boolean removeSuccess = manager.removeCourseFromStudentTimetable(currentuser.getEmail(), courseToRemove);
                    if (removeSuccess) {
                        view.displaySuccess("Course " + courseToRemove + " has been add successfully");
                    } else {
                        view.displayError("Course " + courseToRemove + " add fail");
                    }

                    break;
                case 2: // View Timetable
                    Timetable timetable_view = manager.viewTimetable(currentuser.getEmail());
                    view.displayTimetable(timetable_view);
                    break;
                case 3: // Choose Activity
                    String courseCode = view.getInput("Enter the course code: ");
                    int activity_id;
                    while (true) {
                        try {
                            activity_id = Integer.parseInt(view.getInput("Enter activity id [1(lecture), 2(tutorial) or 3(lab)]: "));
                            if (activity_id == 1 || activity_id == 2 || activity_id == 3) {
                                break;
                            } else {
                                view.displayError("Invalid activity id. It must be 1, 2, or 3.");
                            }
                        } catch (NumberFormatException e) {
                            view.displayError("Invalid input. Please enter a valid number.");
                        }
                    }
                    String status;
                    while (true) {
                        status = view.getInput("Enter the status you want to set (CHOSEN or UNCHOSEN): ");
                        if ("CHOSEN".equalsIgnoreCase(status) || "UNCHOSEN".equalsIgnoreCase(status)) {
                            break;
                        } else {
                            view.displayError("Invalid status entered. Please enter CHOSEN or UNCHOSEN.");
                        }
                    }
                    boolean activityChosen = manager.chooseActivityForCourse(currentuser.getEmail(), courseCode, activity_id, status);
                    if (activityChosen) {
                        view.displaySuccess("Activity set successfully for course " + courseCode);
                    } else {
                        view.displayError("Failed to set activity for course " + courseCode);
                    }
                    break;
                case 4: // Return
                    running = false;
                    break;
            }
        }
    }
}
