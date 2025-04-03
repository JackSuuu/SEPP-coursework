package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;
import java.util.ArrayList;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import java.io.IOException;

public class AdminStaffControllerLegacy extends StaffController {
    public AdminStaffControllerLegacy(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    AuthenticatedUser currentuser = (AuthenticatedUser) sharedContext.getCurrentUser();

    public void manageFAQ() {
        FAQSection currentSection = null;

        while (true) {
            if (currentSection == null) {
                // Use FAQManager’s getRootSections() method.
                List<FAQSection> rootSections = sharedContext.getFaqManager().getRootSections();
                if (rootSections.size() > 0) {
                    view.displayDivider();
                    view.displayInfo("Topic option:");
                }
                for (int i = 0; i < rootSections.size(); i++) {
                    view.displayInfo("[" + i + "] " + rootSections.get(i).getTopic());
                }
                view.displayDivider();
                view.displayInfo("[-1] Return to main menu");
            } else {
                view.displayFAQSection(currentSection);
                view.displayInfo("[-1] Return to " + (currentSection.getParent() == null ? "FAQ main menu" : currentSection.getParent().getTopic()));
            }
            view.displayInfo("[-2] Add FAQ item");
            view.displayInfo("[-3] Remove FAQ item");
            String input = view.getInput("Please choose an option: ");
            try {
                int optionNo = Integer.parseInt(input);

                if (optionNo == -2) {
                    addFAQItem(currentSection);
                } else if (optionNo == -3) {
                    removeFAQItem(currentSection);
                } else if (optionNo == -1) {
                    if (currentSection == null) {
                        break;
                    } else {
                        currentSection = currentSection.getParent();
                    }
                } else {
                    try {
                        if (currentSection == null) {
                            currentSection = sharedContext.getFaqManager().getRootSections().get(optionNo);
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
        // When adding an item at the root of FAQ, creating a topic is mandatory.
        boolean createSection = (currentSection == null);
        if (!createSection) {
            createSection = view.getYesNoInput("Would you like to create a new topic for the FAQ item?");
        }

        if (createSection) {
            String newTopic = view.getInput("Enter new topic title: ");
            FAQSection newSection;
            if (currentSection == null) {
                // Check if the topic exists in the root sections.
                FAQSection existing = sharedContext.getFaqManager().getSectionByTopic(newTopic);
                if (existing != null && existing.getParent() == null) {
                    view.displayWarning("Topic '" + newTopic + "' already exists!");
                    newSection = existing;
                } else {
                    newSection = sharedContext.getFaqManager().addTopic(newTopic);
                    view.displayInfo("Created topic '" + newTopic + "'");
                }
            } else {
                // Check if the topic exists under the current section.
                boolean exists = false;
                FAQSection found = null;
                for (FAQSection subsection : currentSection.getSubsections()) {
                    if (subsection.getTopic().equals(newTopic)) {
                        exists = true;
                        found = subsection;
                        break;
                    }
                }
                if (exists) {
                    view.displayWarning("Topic '" + newTopic + "' already exists under '" + currentSection.getTopic() + "'!");
                    newSection = found;
                } else {
                    newSection = sharedContext.getFaqManager().addSubtopic(currentSection, newTopic);
                    view.displayInfo("Created topic '" + newTopic + "' under '" + currentSection.getTopic() + "'");
                }
            }
            currentSection = newSection;
        }

        String question = view.getInput("Enter the question for new FAQ item: ");
        String answer = view.getInput("Enter the answer for new FAQ item: ");
        // Use FAQManager to add the FAQ item.
        sharedContext.getFaqManager().addFAQItem(currentSection, question, answer, null);

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

    private void removeFAQItem(FAQSection currentSection) {
        if (currentSection == null) {
            view.displayError("No topic selected. Cannot remove a FAQ item.");
            return;
        }
        List<FAQItem> items = currentSection.getItems();
        if (items.isEmpty()) {
            view.displayWarning("There are no FAQ items in this topic.");
            return;
        }
        view.displayInfo("FAQ Items:");
        for (int i = 0; i < items.size(); i++) {
            view.displayInfo("[" + i + "] " + items.get(i).getQuestion());
        }
        String input = view.getInput("Enter the index of the FAQ item to remove: ");
        try {
            int itemIndex = Integer.parseInt(input);
            if (itemIndex >= 0 && itemIndex < items.size()) {
                items.remove(itemIndex);
                view.displaySuccess("FAQ item removed successfully.");

                // If removal results in no more FAQ items, remove the entire topic and move up subtopics.
                if (items.isEmpty()) {
                    FAQSection parent = currentSection.getParent();
                    if (parent != null) {
                        // Move subtopics up under the parent topic.
                        parent.getSubsections().addAll(currentSection.getSubsections());
                        parent.getSubsections().remove(currentSection);
                    } else {
                        // When the topic is a root section.
                        List<FAQSection> rootSections = sharedContext.getFaqManager().getRootSections();
                        rootSections.addAll(currentSection.getSubsections());
                        rootSections.remove(currentSection);
                    }
                    view.displaySuccess("Topic '" + currentSection.getTopic() + "' removed as it contained no FAQ items. Its subtopics were moved up one level.");
                }
            } else {
                view.displayError("Invalid index: " + itemIndex);
            }
        } catch (NumberFormatException e) {
            view.displayError("Invalid input. Please enter a valid number.");
        }
    }

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

                    String courseCode;
                    do {
                        courseCode = view.getInput("Enter course code: ");
                        if (courseCode.trim().isEmpty()) {
                            view.displayError("Course code cannot be empty.");
                        }
                    } while (courseCode.trim().isEmpty());

                    String name;
                    do {
                        name = view.getInput("Enter course name: ");
                        if (name.trim().isEmpty()) {
                            view.displayError("Course name cannot be empty.");
                        }
                    } while (name.trim().isEmpty());

                    String description;
                    do {
                        description = view.getInput("Enter course description: ");
                        if (description.trim().isEmpty()) {
                            view.displayError("Course description cannot be empty.");
                        }
                    } while (description.trim().isEmpty());

                    Boolean requiresComputer = view.getYesNoInput("Does it require a computer?");

                    String courseOrganiserName;
                    do {
                        courseOrganiserName = view.getInput("Enter course organiser name: ");
                        if (courseOrganiserName.trim().isEmpty()) {
                            view.displayError("Course organiser name cannot be empty.");
                        }
                    } while (courseOrganiserName.trim().isEmpty());

                    String courseOrganiserEmail;
                    do {
                        courseOrganiserEmail = view.getInput("Enter course organiser Email: ");
                        if (courseOrganiserEmail.trim().isEmpty()) {
                            view.displayError("Course organiser Email cannot be empty.");
                        }
                    } while (courseOrganiserEmail.trim().isEmpty());

                    String courseSecretaryName;
                    do {
                        courseSecretaryName = view.getInput("Enter course secretary name: ");
                        if (courseSecretaryName.trim().isEmpty()) {
                            view.displayError("Course secretary name cannot be empty.");
                        }
                    } while (courseSecretaryName.trim().isEmpty());

                    String courseSecretaryEmail;
                    do {
                        courseSecretaryEmail = view.getInput("Enter course secretary Email: ");
                        if (courseSecretaryEmail.trim().isEmpty()) {
                            view.displayError("Course secretary Email cannot be empty.");
                        }
                    } while (courseSecretaryEmail.trim().isEmpty());

                    int requiredTutorials = -1;
                    while (true) {
                        String tutorialsInput = view.getInput("Enter the number of Tutorials required: ");
                        try {
                            requiredTutorials = Integer.parseInt(tutorialsInput);
                            if (requiredTutorials < 0) {
                                view.displayError("Number of Tutorials cannot be negative.");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            view.displayError("Invalid number. Please enter a valid integer for Tutorials.");
                        }
                    }

                    int requiredLabs = -1;
                    while (true) {
                        String labsInput = view.getInput("Enter the number of Labs required: ");
                        try {
                            requiredLabs = Integer.parseInt(labsInput);
                            if (requiredLabs < 0) {
                                view.displayError("Number of Labs cannot be negative.");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            view.displayError("Invalid number. Please enter a valid integer for Labs.");
                        }
                    }

                    manager.addCourse(courseCode, name, description, requiresComputer, courseOrganiserName, courseOrganiserEmail, courseSecretaryName, courseSecretaryEmail, requiredTutorials, requiredLabs);
                    view.displaySuccess("Course added successfully.");
                    email.sendEmail(currentuser.getEmail(), courseOrganiserEmail, name, description);
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
                        String startDate, startTime, endDate, endTime;

                        while (true) {
                            startDate = view.getInput("Enter start date in format [yyyy-MM-dd]: ");
                            try {
                                LocalDate.parse(startDate);
                                break;
                            } catch (Exception e) {
                                view.displayError("Invalid date format. Please try again.");
                            }
                        }

                        while (true) {
                            startTime = view.getInput("Enter start time in format [HH:mm:ss]: ");
                            try {
                                LocalTime.parse(startTime);
                                break;
                            } catch (Exception e) {
                                view.displayError("Invalid time format. Please try again.");
                            }
                        }

                        while (true) {
                            endDate = view.getInput("Enter end date in format [yyyy-MM-dd]: ");
                            try {
                                LocalDate.parse(endDate);
                                break;
                            } catch (Exception e) {
                                view.displayError("Invalid date format. Please try again.");
                            }
                        }

                        while (true) {
                            endTime = view.getInput("Enter end Time in format [HH:mm:ss]: ");
                            try {
                                LocalTime.parse(endTime);
                                break;
                            } catch (Exception e) {
                                view.displayError("Invalid time format. Please try again.");
                            }
                        }
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

                        if (activityType != null &&
                                (activityType.equalsIgnoreCase("LECTURE") || activityType.equalsIgnoreCase("LAB") || activityType.equalsIgnoreCase("TUTORIAL"))) {
                            if (activityType.equalsIgnoreCase("LECTURE")) {
                                recordingEnabled = view.getYesNoInput("Is recording enabled?");
                            } else {
                                capacity = Integer.parseInt(view.getInput("Enter capacity: "));
                            }
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
                        view.displayError("Activity added failed.");
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
                        view.displaySuccess("Activities removed successfully.");
                    } else {
                        view.displayError("Activities removed failed.");
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
