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

public class AdminStaffController extends StaffController {
    public AdminStaffController(SharedContext sharedContext, View view, AuthenticationService auth, EmailService email) {
        super(sharedContext, view, auth, email);
    }

    AuthenticatedUser currentuser = (AuthenticatedUser) sharedContext.getCurrentUser();

    /**
     * MANAGE FAQ
     * Entry point for managing FAQ sections and items.
     * Allows navigating between topics, adding, and removing FAQ entries.
     */
    public void manageFAQ() {
        FAQSection currentSection = null;

        while (true) {
            displayFAQMenu(currentSection);

            String input = view.getInput("Please choose an option: ");
            int optionNo;

            try {
                optionNo = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                view.displayError("Invalid option: " + input);
                continue;
            }

            // Perform action based on menu selection
            switch (optionNo) {
                case -2 -> addFAQItem(currentSection); // Add item to current section
                case -3 -> removeFAQItem(currentSection); // Remove item from current section
                case -1 -> {
                    if (currentSection == null) return; // Exit if at root
                    currentSection = currentSection.getParent(); // Go up one level
                }
                default -> currentSection = navigateToSection(currentSection, optionNo); // Go deeper into subsections
            }
        }
    }

    /**
     * Displays the FAQ menu for the current context (root or a specific section).
     *
     * @param currentSection The section currently being viewed, or null for root.
     */
    private void displayFAQMenu(FAQSection currentSection) {
        if (currentSection == null) {
            List<FAQSection> rootSections = sharedContext.getFaqManager().getRootSections();
            if (!rootSections.isEmpty()) {
                view.displayDivider();
                view.displayInfo("Topic option:");
                for (int i = 0; i < rootSections.size(); i++) {
                    view.displayInfo("[" + i + "] " + rootSections.get(i).getTopic());
                }
                view.displayDivider();
            }
            view.displayInfo("[-1] Return to main menu");
        } else {
            view.displayFAQSection(currentSection);
            String returnLabel = currentSection.getParent() == null ? "FAQ main menu" : currentSection.getParent().getTopic();
            view.displayInfo("[-1] Return to " + returnLabel);
        }

        // General options available at any level
        view.displayInfo("[-2] Add FAQ item");
        view.displayInfo("[-3] Remove FAQ item");
    }

    /**
     * Attempts to navigate to a subsection or root section based on user selection index.
     *
     * @param currentSection The current section the user is in (null if at root).
     * @param index          The index chosen by the user.
     * @return The new current section if navigation is valid; otherwise, the original section.
     */
    private FAQSection navigateToSection(FAQSection currentSection, int index) {
        try {
            return currentSection == null
                    ? sharedContext.getFaqManager().getRootSections().get(index)
                    : currentSection.getSubsections().get(index);
        } catch (IndexOutOfBoundsException e) {
            view.displayError("Invalid option: " + index);
            return currentSection;
        }
    }



    /**
     * ADD FAQ ITEM
     * Adds an FAQ Q&A pair to the system, optionally under a new or existing topic (FAQ section).
     *
     * Helper functions:
     * - createOrFetchTopic
     * - sendFAQUpdateEmails
     * - buildFAQEmailContent
     */
    private void addFAQItem(FAQSection currentSection) {
        view.displayInfo("=== Add New FAQ Question-Answer Pair===");
        // Decide whether to create a new topic section
        boolean createSection = (currentSection == null) || view.getYesNoInput("Would you like to create a new topic for the FAQ item?");
        if (createSection) {
            currentSection = createOrFetchTopic(currentSection);
        }

        // Prompt for FAQ content
        String question;
        while (true) {
            try {
            question = view.getInput("Enter the question for new FAQ item: ");
            if (question.trim().isEmpty()) {
                throw new Exception("FAQ question cannot be empty.");
            }
            break;
            } catch (Exception e) {
            KioskLogger.getInstance().log(currentuser.getEmail(), "addFAQItem", "Empty FAQ question provided", "FAILURE (FAQ question cannot be empty)");
            view.displayError(e.getMessage());
            }
        }
        String answer;
        while (true) {
            try {
            answer = view.getInput("Enter the answer for new FAQ item: ");
            if (answer.trim().isEmpty()) {
                throw new Exception("FAQ answer cannot be empty.");
            }
            break;
            } catch (Exception e) {
            KioskLogger.getInstance().log(currentuser.getEmail(), "addFAQItem", "Empty FAQ answer provided", "FAILURE (FAQ answer cannot be empty)");
            view.displayError(e.getMessage());
            }
        }

        // Get course tag input
        Boolean useCourseTag = view.getYesNoInput("Would you like to add a course tag?");
        if (useCourseTag) {
            String coursesInfo = sharedContext.getCourseManager().viewCourses();
            view.displayInfo("Full Course Details:\n" + coursesInfo);
            for (Course course : sharedContext.getCourseManager().getCourseArray()) {
                StringBuilder activitiesInfo = new StringBuilder("Activities for Course " + course.getCourseCode() + ":\n");
                for (Activity activity : course.getActivities()) {
                    activitiesInfo.append(activity.toString()).append("\n");
                }
                view.displayInfo(activitiesInfo.toString());
            }

            if (coursesInfo.trim().isEmpty()) {
                view.displayInfo("No course available in the system");
            } else {
                view.displayInfo("Available courses: " + coursesInfo);
                view.getInput("Enter course code to add as tag: ");
                String courseTag = view.getInput("Enter course code to add as tag: ");
                if (sharedContext.getCourseManager().hasCode(courseTag)) {
                    sharedContext.getFaqManager().addFAQItem(currentSection, question, answer, courseTag);
                    view.displayInfo("Course '" + courseTag + "' exists and has been tagged.");
                } else {
                    KioskLogger.getInstance().log(currentuser.getEmail(), "addFAQItem", currentSection.getTopic(), "FAILURE (Error: the tag must correspond to a course code)");
                    view.displayError("Course '" + courseTag + "' not found.");
                    return;
                }
            }
        }

        // Add the item under the current section
        sharedContext.getFaqManager().addFAQItem(currentSection, question, answer, null);

        // Notify subscribers
        sendFAQUpdateEmails(currentSection);
        KioskLogger.getInstance().log(currentuser.getEmail(), "addFAQItem", currentSection.getTopic(), "SUCCESS (A new FAQ item was added)");
        view.displaySuccess("The new FAQ item was added");
    }

    /**
     * Creates a new FAQ topic section or fetches an existing one under a parent (if any).
     *
     * @param parentSection The parent section under which to add the topic, or null if root.
     * @return The created or existing FAQSection instance.
     */
    private FAQSection createOrFetchTopic(FAQSection parentSection) {
        String newTopic = view.getInput("Enter new topic title: ");

        // Root-level topic creation
        if (parentSection == null) {
            FAQSection existing = sharedContext.getFaqManager().getSectionByTopic(newTopic);
            if (existing != null && existing.getParent() == null) {
                view.displayWarning("Topic '" + newTopic + "' already exists!");
                return existing;
            }
            view.displaySuccess("Created topic '" + newTopic + "'");
            return sharedContext.getFaqManager().addTopic(newTopic);
        }

        // Check if topic already exists under this parent
        for (FAQSection subsection : parentSection.getSubsections()) {
            if (subsection.getTopic().equals(newTopic)) {
                view.displayWarning("Topic '" + newTopic + "' already exists under '" + parentSection.getTopic() + "'!");
                return subsection;
            }
        }

        view.displayInfo("Created topic '" + newTopic + "' under '" + parentSection.getTopic() + "'");
        return sharedContext.getFaqManager().addSubtopic(parentSection, newTopic);
    }

    /**
     * Sends FAQ update emails to admin and subscribed users.
     *
     * @param section The section whose FAQ content was updated.
     */
    private void sendFAQUpdateEmails(FAQSection section) {
        String subject = "FAQ topic '" + section.getTopic() + "' updated";
        String content = buildFAQEmailContent(section);
        String sender = ((AuthenticatedUser) sharedContext.currentUser).getEmail();

        // Notify admin
        email.sendEmail(sender, SharedContext.ADMIN_STAFF_EMAIL, subject, content);

        // Notify all subscribers
        for (String subscriber : sharedContext.usersSubscribedToFAQTopic(section.getTopic())) {
            email.sendEmail(SharedContext.ADMIN_STAFF_EMAIL, subscriber, subject, content);
        }
    }

    /**
     * Builds the email body content from a section's FAQ items.
     *
     * @param section The FAQ section to extract Q&As from.
     * @return A string representing the full email content.
     */
    private String buildFAQEmailContent(FAQSection section) {
        StringBuilder sb = new StringBuilder("Updated Q&As:");
        for (FAQItem item : section.getItems()) {
            sb.append("\n\nQ: ").append(item.getQuestion())
                    .append("\nA: ").append(item.getAnswer());
        }
        return sb.toString();
    }



    /**
     * REMOVE FAQ ITEM
     * - Removes an FAQ Q-A pair from the given section.
     *
     * Helper functions:
     * - displayFAQItems
     * - isValidIndex
     * - handleEmptyTopicAfterRemoval
     *
     * @param currentSection The FAQ section to remove a Q&A from.
     */
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

        displayFAQItems(items);

        String input = view.getInput("Enter the index of the FAQ item to remove: ");
        try {
            int itemIndex = Integer.parseInt(input);
            if (isValidIndex(itemIndex, items.size())) {
                items.remove(itemIndex);
                view.displaySuccess("FAQ item removed successfully.");
                handleEmptyTopicAfterRemoval(currentSection);
            } else {
                view.displayError("Invalid index: " + itemIndex);
            }
        } catch (NumberFormatException e) {
            view.displayError("Invalid input. Please enter a valid number.");
        }
    }

    /**
     * Displays all FAQ items in a section with their indices.
     *
     * @param items List of FAQ items to display.
     */
    private void displayFAQItems(List<FAQItem> items) {
        view.displayInfo("FAQ Items:");
        for (int i = 0; i < items.size(); i++) {
            view.displayInfo("[" + i + "] " + items.get(i).getQuestion());
        }
    }

    /**
     * Checks if the given index is valid within the bounds of a list.
     *
     * @param index The index to check.
     * @param size  The size of the list.
     * @return true if the index is valid, false otherwise.
     */
    private boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
    }

    /**
     * If a section is empty after item removal, this method:
     * - removes the section,
     * - reattaches its subtopics to the parent or root,
     * - and notifies the user.
     *
     * @param section The FAQ section to evaluate for deletion.
     */
    private void handleEmptyTopicAfterRemoval(FAQSection section) {
        if (!section.getItems().isEmpty()) return;

        FAQSection parent = section.getParent();
        List<FAQSection> subsections = section.getSubsections();

        if (parent != null) {
            parent.getSubsections().addAll(subsections);
            parent.getSubsections().remove(section);
        } else {
            List<FAQSection> rootSections = sharedContext.getFaqManager().getRootSections();
            rootSections.addAll(subsections);
            rootSections.remove(section);
        }

        view.displaySuccess("Topic '" + section.getTopic() + "' removed as it contained no FAQ items. Its subtopics were moved up one level.");
    }



    /**
     * MANAGE INQUIRIES
     * - Presents a list of inquiries to the admin.
     * - Allows redirecting or responding to each inquiry.
     *
     * Helper methods:
     * - handleInquiry(Inquiry)
     */
    public void manageInquiries() {
        while (true) {
            view.displayInfo("Pending inquiries");

            String[] inquiryTitles = getInquiryTitles(sharedContext.inquiries);
            int selection = selectFromMenu(inquiryTitles, "Back to main menu");

            if (selection == -1) return;

            Inquiry selectedInquiry = sharedContext.inquiries.get(selection);
            handleInquiry(selectedInquiry);
        }
    }

    /**
     * Handles follow-up actions for a selected inquiry.
     * Displays options to redirect or respond.
     *
     * @param inquiry the selected inquiry to manage
     */
    private void handleInquiry(Inquiry inquiry) {
        final String REDIRECT = "Redirect inquiry";
        final String RESPOND = "Respond to inquiry";
        String[] followUpOptions = { REDIRECT, RESPOND };

        while (true) {
            view.displayDivider();
            view.displayInquiry(inquiry);
            view.displayDivider();

            int choice = selectFromMenu(followUpOptions, "Back to all inquiries");

            if (choice == -1) return;

            String action = followUpOptions[choice];
            switch (action) {
                case REDIRECT -> redirectInquiry(inquiry);
                case RESPOND -> {
                    respondToInquiry(inquiry);
                    return; // Exit after responding
                }
                default -> view.displayError("Unknown option.");
            }
        }
    }


    /**
     * REDIRECT INQUIRY
     * - Reassigns the selected inquiry to another staff member.
     * - Sends a notification email to the new assignee.
     *
     * @param inquiry the inquiry to be redirected
     */
    private void redirectInquiry(Inquiry inquiry) {
        String newAssignee = view.getInput("Enter assignee email: ");
        inquiry.setAssignedTo(newAssignee);

        email.sendEmail(
                SharedContext.ADMIN_STAFF_EMAIL,
                newAssignee,
                "New inquiry from " + inquiry.getInquirerEmail(),
                "Subject: " + inquiry.getSubject() +
                        "\nPlease log into the Self Service Portal to review and respond to the inquiry."
        );

        view.displaySuccess("Inquiry has been reassigned");
    }



    /**
     * MANAGE COURSE
     * - Provides options to add, remove, and view courses and activities.
     * - Delegates specific actions to helper handlers.
     *
     * Options:
     * 0 - Add a new course
     * 1 - Remove an existing course
     * 2 - Add activity for a course
     * 3 - Remove all activities
     * 4 - View all courses
     * 5 - View all activities
     */
    public void manageCourse() {
        CourseManager manager = sharedContext.getCourseManager();

        while (true) {
            String[] options = {
                    "Add a new course",
                    "Remove an existing course",
                    "Add activity for a course",
                    "Remove all activities",
                    "View all courses",
                    "View all activities"
            };

            int selection = selectFromMenu(options, "Back to main menu");

            try {
                switch (selection) {
                    case -1 -> { return; }
                    case 0 -> handleAddCourse(manager);
                    case 1 -> handleRemoveCourse(manager);
                    case 2 -> handleAddActivity(manager);
                    case 3 -> handleRemoveActivities(manager);
                    case 4 -> System.out.println(manager.viewCourses());
                    case 5 -> handleViewActivities(manager);
                    default -> view.displayError("Invalid option: " + selection);
                }
            } catch (NumberFormatException e) {
                view.displayError("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Adds a new course using user input for all required fields.
     */
    private void handleAddCourse(CourseManager manager) {
        view.displayInfo("=== Add Course ===");
        String code = promptNonEmpty("Enter course code: ", "Course code");
        String name = promptNonEmpty("Enter course name: ", "Course name");
        String description = promptNonEmpty("Enter course description: ", "Description");
        boolean needsComputer;
        while (true) {
            String input = view.getInput("Does it require a computer? (Y/N): ");
            if (input.equalsIgnoreCase("Y")) {
            needsComputer = true;
            break;
            } else if (input.equalsIgnoreCase("N")) {
            needsComputer = false;
            break;
            } else {
            view.displayError("Invalid input. Please enter Y or N.");
            }
        }
        String organiserName = promptNonEmpty("Enter course organiser name: ", "Course organiser name");
        String organiserEmail = promptNonEmpty("Enter course organiser Email: ", "Course organiser email");
        String secretaryName = promptNonEmpty("Enter course secretary name: ", "Course secretary name");
        String secretaryEmail = promptNonEmpty("Enter course secretary Email: ", "Course secretary email");
        int tutorials = promptPositiveInt("Enter the number of Tutorials required: ", "Invalid number. Please enter a valid integer for Tutorials.");
        int labs = promptPositiveInt("Enter the number of Labs required: ", "Invalid number. Please enter a valid integer for Labs.");

        String courseInfo = "Code: " + code +
            ", Name: " + name +
            ", Description: " + description +
            ", Needs Computer: " + needsComputer +
            ", Organiser Name: " + organiserName +
            ", Organiser Email: " + organiserEmail +
            ", Secretary Name: " + secretaryName +
            ", Secretary Email: " + secretaryEmail +
            ", Tutorials: " + tutorials +
            ", Labs: " + labs;

        email.sendEmail(currentuser.getEmail(), organiserEmail, name, description);
        
        if(code.trim().isEmpty() || name.trim().isEmpty() || description.trim().isEmpty() ||
           organiserName.trim().isEmpty() || organiserEmail.trim().isEmpty() ||
           secretaryName.trim().isEmpty() || secretaryEmail.trim().isEmpty()) {
            KioskLogger.getInstance().log(currentuser.getEmail(), "addCourse", courseInfo, "FAILURE (Error: Required course info not provided)");
            view.displayError("Required course in not provided");
        } else {
            manager.addCourse(code, name, description, needsComputer, organiserName, organiserEmail, secretaryName, secretaryEmail, tutorials, labs, view);
            KioskLogger.getInstance().log(currentuser.getEmail(), "addCourse", courseInfo, "SUCCESS (Course has been successfully created)");
            view.displaySuccess("Course added successfully.");
        }

    }

    /**
     * Removes a course if it exists.
     */
    private void handleRemoveCourse(CourseManager manager) {
        String code = view.getInput("Enter course code for your course want to remove: ");
        if (manager.removeCourse(code)) {
            view.displaySuccess("Course removed successfully.");
        } else {
            view.displayError("Course not found.");
        }
    }

    /**
     * Adds a new activity to a course.
     */
    private void handleAddActivity(CourseManager manager) {
        view.displayInfo("=== Add Course - Activities===");
        String code = view.getInput("Enter course code for the course you want to add activities: ");
        Course course = findCourse(manager, code);

        if (course == null) {
            view.displayError("Course not found.");
            return;
        }

        view.displayInfo("Enter all activity info about the course:");
        LocalDate startDate = promptDate("Enter start date [yyyy-MM-dd]: ");
        LocalTime startTime = promptTime("Enter start time [HH:mm:ss]: ");
        LocalDate endDate = promptDate("Enter end date [yyyy-MM-dd]: ");
        LocalTime endTime = promptTime("Enter end time [HH:mm:ss]: ");
        String location = view.getInput("Enter location: ");
        DayOfWeek day = promptDay();
        String type = view.getInput("Enter activity type (LAB, TUTORIAL, or LECTURE): ");
        int id = 0;

        int capacity = -1;
        boolean recording = false;

        // automatically generated activity id based on different type
        if ("LECTURE".equalsIgnoreCase(type)) {
            recording = view.getYesNoInput("Is recording enabled?");
            id = 1;
        } else if ("LAB".equalsIgnoreCase(type)) {
            capacity = Integer.parseInt(view.getInput("Enter capacity: "));
            id = 2;
        } else if ("TUTORIAL".equalsIgnoreCase(type)) {
            capacity = Integer.parseInt(view.getInput("Enter capacity: "));
            id = 3;
        }
        
        else {
            view.displayError("Invalid activity type.");
            return;
        }

        String activityInfo = "ID: " + id +
            ", Type: " + type +
            ", Start Date: " + startDate +
            ", Start Time: " + startTime +
            ", End Date: " + endDate +
            ", End Time: " + endTime +
            ", Location: " + location +
            ", Day: " + day;

        course.addActivity(id, type.toUpperCase(), startDate, startTime, endDate, endTime, location, day, capacity, recording);
        KioskLogger.getInstance().log(currentuser.getEmail(), "addCourse", activityInfo, "SUCCESS (New course activity added)");
        view.displaySuccess("Activity added successfully.");
    }

    /**
     * Removes all activities from a specified course.
     */
    private void handleRemoveActivities(CourseManager manager) {
        String code = view.getInput("Enter course code for your course activities you want to remove: ");
        Course course = findCourse(manager, code);

        if (course != null) {
            course.removeActivities();
            view.displaySuccess("Activities removed successfully.");
        } else {
            view.displayError("Course not found.");
        }
    }

    /**
     * Displays all activities for a specified course.
     */
    private void handleViewActivities(CourseManager manager) {
        String code = view.getInput("Enter course code to view activities: ");
        Course course = findCourse(manager, code);

        if (course != null) {
            course.viewActivities();
        } else {
            view.displayError("Course not found.");
        }
    }

    /**
     * Finds a course by course code.
     */
    private Course findCourse(CourseManager manager, String code) {
        for (Course c : manager.getCourseArray()) {
            if (c.getCourseCode().equals(code)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Prompts the user for non-empty input with a customizable error message.
     *
     * @param message Prompt message for the user.
     * @param emptyField to display the empty field that leads to the error.
     * @return Valid non-empty input.
     */
    private String promptNonEmpty(String message, String emptyField) {
        String input;
        do {
            input = view.getInput(message);
            if (input.trim().isEmpty()) {
                view.displayError(emptyField + " cannot be empty.");
            }
        } while (input.trim().isEmpty());
        return input;
    }


    /**
     * Prompts for a positive integer input.
     */
    private int promptPositiveInt(String message, String errorMsg) {
        while (true) {
            try {
                int value = Integer.parseInt(view.getInput(message));
                if (value >= 0) return value;
                view.displayError("Value must be non-negative.");
            } catch (NumberFormatException e) {
                view.displayError(errorMsg);
            }
        }
    }

    /**
     * Prompts for a valid LocalDate.
     */
    private LocalDate promptDate(String message) {
        while (true) {
            try {
                return LocalDate.parse(view.getInput(message));
            } catch (Exception e) {
                view.displayError("Invalid date format.");
            }
        }
    }

    /**
     * Prompts for a valid LocalTime.
     */
    private LocalTime promptTime(String message) {
        while (true) {
            try {
                return LocalTime.parse(view.getInput(message));
            } catch (Exception e) {
                view.displayError("Invalid time format.");
            }
        }
    }

    /**
     * Prompts for a valid DayOfWeek.
     */
    private DayOfWeek promptDay() {
        try {
            return DayOfWeek.valueOf(view.getInput("Enter day (e.g., MONDAY): ").toUpperCase());
        } catch (Exception e) {
            view.displayError("Invalid day. Defaulting to MONDAY.");
            return DayOfWeek.MONDAY;
        }
    }

}
