package controller;

import external.AuthenticationService;
import external.EmailService;
import model.CourseManager;
import model.SharedContext;
import view.View;

public class ViewerController extends Controller {
    public ViewerController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    public void viewCourses() {
        // Create an instance of CourseManager
        CourseManager manager = new CourseManager(); 
    
        // Call the method on the instance
        String courses = manager.viewCourses();
    
        // Display courses
        System.out.println(courses);
    }

    public void viewSpecificCourse(String courseCode) {
        // Create an instance of CourseManager
        CourseManager manager = new CourseManager();

        // Call the method on the instance
        String course = manager.viewCourse(courseCode);

        // Display courses
        System.out.println(course);
    }
}
