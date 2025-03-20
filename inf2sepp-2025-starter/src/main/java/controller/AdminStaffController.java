package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;
import java.util.ArrayList;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import java.io.IOException;

public class AdminStaffController extends StaffController {
    public AdminStaffController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    public void manageFAQ() {
        FAQSection currentSection = null;

        while (true) {
            if (currentSection == null) {
                view.displayFAQ(sharedContext.getFAQ());
                view.displayInfo("[-1] Return to main menu");
            } else {
                view.displayFAQSection(currentSection);
                view.displayInfo("[-1] Return to " + (currentSection.getParent() == null ? "FAQ" : currentSection.getParent().getTopic()));
            }
            view.displayInfo("[-2] Add FAQ item");
            String input = view.getInput("Please choose an option: ");
            try {
                int optionNo = Integer.parseInt(input);

                if (optionNo == -2) {
                    addFAQItem(currentSection);
                } else if (optionNo == -1) {
                    if (currentSection == null) {
                        break;
                    } else {
                        currentSection = currentSection.getParent();
                    }
                } else {
                    try {
                        if (currentSection == null) {
                            currentSection = sharedContext.getFAQ().getSections().get(optionNo);
                        } else {
                            currentSection = currentSection.getSubsections().get(optionNo);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        view.displayError("Invalid option: " + optionNo);
                    }
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + input);
            }
        }
    }

    private void addFAQItem(FAQSection currentSection) {
        // When adding an item at root of FAQ, creating a section is mandatory
        boolean createSection = (currentSection == null);
        if (!createSection) {
            createSection = view.getYesNoInput("Would you like to create a new topic for the FAQ item?");
        }

        if (createSection) {
            String newTopic = view.getInput("Enter new topic title: ");
            FAQSection newSection = new FAQSection(newTopic);
            if (currentSection == null) {
                if (sharedContext.getFAQ().getSections().stream().anyMatch(section -> section.getTopic().equals(newTopic))) {
                    view.displayWarning("Topic '" + newTopic + "' already exists!");
                    newSection = sharedContext.getFAQ().getSections().stream().filter(section -> section.getTopic().equals(newTopic)).findFirst().orElseThrow();
                } else {
                    sharedContext.getFAQ().addSection(newSection);
                    view.displayInfo("Created topic '" + newTopic + "'");
                }
            } else {
                if (currentSection.getSubsections().stream().anyMatch(section -> section.getTopic().equals(newTopic))) {
                    view.displayWarning("Topic '" + newTopic + "' already exists under '" + currentSection.getTopic() + "'!");
                    newSection = currentSection.getSubsections().stream().filter(section -> section.getTopic().equals(newTopic)).findFirst().orElseThrow();
                } else {
                    currentSection.addSubsection(newSection);
                    view.displayInfo("Created topic '" + newTopic + "' under '" + currentSection.getTopic() + "'");
                }
            }
            currentSection = newSection;
        }

        String question = view.getInput("Enter the question for new FAQ item: ");
        String answer = view.getInput("Enter the answer for new FAQ item: ");
        currentSection.getItems().add(new FAQItem(question, answer));

        String emailSubject = "FAQ topic '" + currentSection.getTopic() + "' updated";
        StringBuilder emailContentBuilder = new StringBuilder();
        emailContentBuilder.append("Updated Q&As:");
        for (FAQItem item : currentSection.getItems()) {
            emailContentBuilder.append("\n\n");
            emailContentBuilder.append("Q: ");
            emailContentBuilder.append(item.getQuestion());
            emailContentBuilder.append("\n");
            emailContentBuilder.append("A: ");
            emailContentBuilder.append(item.getAnswer());
        }
        String emailContent = emailContentBuilder.toString();

        email.sendEmail(
                ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
                SharedContext.ADMIN_STAFF_EMAIL,
                emailSubject,
                emailContent
        );
        for (String subscriberEmail : sharedContext.usersSubscribedToFAQTopic(currentSection.getTopic())) {
            email.sendEmail(
                    SharedContext.ADMIN_STAFF_EMAIL,
                    subscriberEmail,
                    emailSubject,
                    emailContent
            );
        }
        view.displaySuccess("Created new FAQ item");
    }

    // TODO: remove FAQ item to be implemented
    // private void removeFAQItem(FAQSection currentSection) {

    // }

    public void manageInquiries() {
        String[] inquiryTitles = getInquiryTitles(sharedContext.inquiries);

        while (true) {
            view.displayInfo("Pending inquiries");
            int selection = selectFromMenu(inquiryTitles, "Back to main menu");
            if (selection == -1) {
                return;
            }
            Inquiry selectedInquiry = sharedContext.inquiries.get(selection);

            while (true) {
                view.displayDivider();
                view.displayInquiry(selectedInquiry);
                view.displayDivider();
                String[] followUpOptions = { "Redirect inquiry", "Respond to inquiry" };
                int followUpSelection = selectFromMenu(followUpOptions, "Back to all inquiries");

                if (followUpSelection == -1) {
                    break;
                } else if (followUpOptions[followUpSelection].equals("Redirect inquiry")) {
                    redirectInquiry(selectedInquiry);
                } else if (followUpOptions[followUpSelection].equals("Respond to inquiry")) {
                    respondToInquiry(selectedInquiry);
                    inquiryTitles = getInquiryTitles(sharedContext.inquiries); // required to remove responded inquiry from titles
                    break;
                }
            }
        }
    }

    private void redirectInquiry(Inquiry inquiry) {
        inquiry.setAssignedTo(view.getInput("Enter assignee email: "));
        email.sendEmail(
                SharedContext.ADMIN_STAFF_EMAIL,
                inquiry.getAssignedTo(),
                "New inquiry from " + inquiry.getInquirerEmail(),
                "Subject: " + inquiry.getSubject() + "\nPlease log into the Self Service Portal to review and respond to the inquiry."
        );
        view.displaySuccess("Inquiry has been reassigned");
    }

    public void manageCourse() {
        while (true) {
            String[] options = {"Add a new course", "Remove an existing course", "Add activity for a course", "Remove all activities", "View all courses", "View all activities"};
            int selection = selectFromMenu(options, "Back to main menu");

            try {
                if (selection == -1) {
                    return;
                } else if (selection == 0) {
                    // Add new course
                    CourseManager manager = sharedContext.getCourseManager();
    
                    String courseCode = view.getInput("Enter course code: ");
                    String Name = view.getInput("Enter course name: ");
                    String Description = view.getInput("Enter course description: ");
                    Boolean requiresComputer = view.getYesNoInput("Does it requires computer?");
                    String courseOrganiserName = view.getInput("Enter course organiser name: ");
                    String courseOrganiserEmail = view.getInput("Enter course organiser Email: ");
                    String courseSecretaryName = view.getInput("Enter course secretary name: ");
                    String courseSecretaryEmail = view.getInput("Enter course secretary Email: ");
                    int requiredTutorials = Integer.parseInt(view.getInput("Enter the number of Tutorials required: "));
                    int requiredLabs = Integer.parseInt(view.getInput("Enter the number of Labs required: "));
    
                    manager.addCourse(courseCode, Name, Description, requiresComputer, courseOrganiserName, courseOrganiserEmail, courseSecretaryName, courseSecretaryEmail, requiredTutorials, requiredLabs);
                    view.displaySuccess("Course added successfully.");
                } else if (selection == 1) {
                    // Remove course
                    CourseManager manager = sharedContext.getCourseManager();

                    String courseCode = view.getInput("Enter course code for your course want to remove: ");
                    boolean removed = manager.removeCourse(courseCode);
                    if (removed) {
                        view.displaySuccess("Course removed successfully.");
                    } else {
                        view.displayError("Course not found.");
                    }

                } else if (selection == 2) {
                    CourseManager manager = sharedContext.getCourseManager();

                    String courseCode = view.getInput("Enter course code for the course you want to add activities: ");
                    ArrayList<Course> courses = manager.getCourseArray();

                    boolean activityAdded = false;
                    Course targetCourse = null;
                    for (Course course : courses) {
                        if (course.getCourseCode().equals(courseCode)) {
                            targetCourse = course;
                            break;
                        }
                    }
                    if (targetCourse != null) {
                        int activity_id = Integer.parseInt(view.getInput("Enter activity id: "));
                        String startDate = view.getInput("Enter start date in format [yyyy-MM-dd]: ");
                        String startTime = view.getInput("Enter start time in format [HH:mm:ss]: ");
                        String endDate = view.getInput("Enter end date in format [yyyy-MM-dd]: ");
                        String endTime = view.getInput("Enter end Time in format [HH:mm:ss]: ");
                        String location = view.getInput("Enter location: ");
                        DayOfWeek day;
                        try {
                            day = DayOfWeek.valueOf(view.getInput("Enter day (e.g., MONDAY): ").toUpperCase());
                        } catch (IllegalArgumentException e) {
                            view.displayError("Invalid day entered. Defaulting to MONDAY.");
                            day = DayOfWeek.MONDAY;
                        }
                        LocalDate startLocalDate = LocalDate.parse(startDate);
                        LocalTime startLocalTime = LocalTime.parse(startTime);
                        LocalDate endLocalDate = LocalDate.parse(endDate);
                        LocalTime endLocalTime = LocalTime.parse(endTime);
                        int capacity = -1;
                        boolean recordingEnabled = false;


                        String activityType = view.getInput("Enter activity type (LAB, TUTORIAL, or LECTURE): ");
                        
                        if (activityType.equalsIgnoreCase("LECTURE")) {
                            recordingEnabled = view.getYesNoInput("Is recording enabled?");
                        } else {
                            capacity = Integer.parseInt(view.getInput("Enter capacity: "));
                        }
                        
                        if (activityType != null && 
                            (activityType.equalsIgnoreCase("LECTURE") || activityType.equalsIgnoreCase("LAB") || activityType.equalsIgnoreCase("TUTORIAL"))) {
                            activityType.toUpperCase(); // ensure the type is valid
                            targetCourse.addActivity(activity_id, activityType, startLocalDate, startLocalTime, endLocalDate, endLocalTime, location, day, capacity, recordingEnabled);
                            activityAdded = true;
                        } else {
                            view.displayError("Invalid activity type entered. Allowed types: LECTURE, LAB, or TUTORIAL.");
                        }
                    }

                    if (activityAdded) {
                        view.displaySuccess("Activity added successfully.");
                    } else {
                        view.displayError("Course not found.");
                    }

                } else if (selection == 3) {
                    CourseManager manager = sharedContext.getCourseManager();

                    String courseCode = view.getInput("Enter course code for your course activities you want to remove: ");
                    ArrayList<Course> courses = manager.getCourseArray();

                    boolean activitesRemoved = false;
                    Course targetCourse = null;
                    for (Course course : courses) {
                        if (course.getCourseCode().equals(courseCode)) {
                            targetCourse = course;
                            break;
                        }
                    }
                    if (targetCourse != null) {
                        targetCourse.removeActivities();
                        activitesRemoved = ! activitesRemoved;
                    }

                    if (activitesRemoved) {
                        view.displaySuccess("Activities added successfully.");
                    } else {
                        view.displayError("Course not found.");
                    }

                } else if (selection == 4) {
                    // View all courses
                    // Create an instance of CourseManager
                    CourseManager manager = sharedContext.getCourseManager();
                
                    // Call the method on the instance
                    String courses = manager.viewCourses();
                
                    // Display courses
                    System.out.println(courses);
                } else if (selection == 5) {
                    // View all activities
                     // Create an instance of CourseManager
                     CourseManager manager = sharedContext.getCourseManager();
                     String courseCode = view.getInput("Enter course code for your course activities you want to view: ");
                     ArrayList<Course> courses = manager.getCourseArray();
 
                     Course targetCourse = null;
                     for (Course course : courses) {
                         if (course.getCourseCode().equals(courseCode)) {
                             targetCourse = course;
                             break;
                         }
                     }
                     if (targetCourse != null) {
                        targetCourse.viewActivities();;
                    } else {
                        view.displayError("Course not found.");
                    }

                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + selection);
            }

        }
    }
 
}
