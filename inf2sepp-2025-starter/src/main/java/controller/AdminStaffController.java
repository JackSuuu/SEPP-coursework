package controller;

import external.AuthenticationService;
import external.EmailService;
import model.*;
import view.View;

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
            String[] options = {"Add a new course", "Remove an existing course", "View all courses"};
            int selection = selectFromMenu(options, "Back to main menu");
            
            if (selection == -1) {
                return;
            } else if (selection == 0) {
                // Add new course
                addCourse();
            } else if (selection == 1) {
                // Remove course
                removeCourse();
            } else if (selection == 2) {
                // View all courses
                viewAllCourses();
            }
        }
    }
    
    private void addCourse() {
        String courseCode = view.getInput("Enter course code: ");
        // Check if course already exists
        if (sharedContext.getCourses().stream().anyMatch(c -> c.getCourseCode().equals(courseCode))) {
            view.displayError("A course with this code already exists.");
            return;
        }
        
        String name = view.getInput("Enter course name: ");
        String description = view.getInput("Enter course description: ");
        boolean requiresComputer = view.getYesNoInput("Does this course require a computer?");
        String courseOrganiserName = view.getInput("Enter course organiser name: ");
        String courseOrganiserEmail = view.getInput("Enter course organiser email: ");
        String courseSecretaryName = view.getInput("Enter course secretary name: ");
        String courseSecretaryEmail = view.getInput("Enter course secretary email: ");
        
        int requiredTutorials = -1;
        while (requiredTutorials < 0) {
            try {
                requiredTutorials = Integer.parseInt(view.getInput("Enter number of required tutorials: "));
                if (requiredTutorials < 0) {
                    view.displayError("Number of tutorials cannot be negative.");
                }
            } catch (NumberFormatException e) {
                view.displayError("Please enter a valid number.");
            }
        }
        
        int requiredLabs = -1;
        while (requiredLabs < 0) {
            try {
                requiredLabs = Integer.parseInt(view.getInput("Enter number of required labs: "));
                if (requiredLabs < 0) {
                    view.displayError("Number of labs cannot be negative.");
                }
            } catch (NumberFormatException e) {
                view.displayError("Please enter a valid number.");
            }
        }
        
        // Create and add the new course
        Course newCourse = new Course(
            courseCode, 
            name, 
            description, 
            requiresComputer, 
            courseOrganiserName,
            courseOrganiserEmail,
            courseSecretaryName,
            courseSecretaryEmail,
            requiredTutorials,
            requiredLabs
        );
        
        sharedContext.addCourse(newCourse);
        
        // Send notification email to admin staff
        String emailSubject = "New Course Added: " + courseCode;
        String emailContent = "A new course has been added to the system:\n\n" +
                             "Course Code: " + courseCode + "\n" +
                             "Name: " + name + "\n" +
                             "Description: " + description + "\n" +
                             "Requires Computer: " + (requiresComputer ? "Yes" : "No") + "\n" +
                             "Organiser: " + courseOrganiserName + " (" + courseOrganiserEmail + ")\n" +
                             "Secretary: " + courseSecretaryName + " (" + courseSecretaryEmail + ")\n" +
                             "Required Tutorials: " + requiredTutorials + "\n" +
                             "Required Labs: " + requiredLabs;
                             
        email.sendEmail(
            ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
            SharedContext.ADMIN_STAFF_EMAIL,
            emailSubject,
            emailContent
        );
        
        view.displaySuccess("Course " + courseCode + " has been successfully added.");
    }
    
    private void removeCourse() {
        if (sharedContext.getCourses().isEmpty()) {
            view.displayWarning("There are no courses to remove.");
            return;
        }
        
        // Display courses and let user select one to remove
        String[] courseOptions = sharedContext.getCourses().stream()
                .map(c -> c.getCourseCode() + " - " + c.getName())
                .toArray(String[]::new);
                
        int selection = selectFromMenu(courseOptions, "Cancel");
        if (selection == -1) {
            return;
        }
        
        Course selectedCourse = sharedContext.getCourses().get(selection);
        String courseCode = selectedCourse.getCourseCode();
        
        // Confirm deletion
        boolean confirm = view.getYesNoInput("Are you sure you want to remove course " + courseCode + "?");
        if (!confirm) {
            view.displayInfo("Course removal cancelled.");
            return;
        }
        
        // Remove the course
        sharedContext.removeCourse(courseCode);
        
        // Send notification email
        String emailSubject = "Course Removed: " + courseCode;
        String emailContent = "The following course has been removed from the system:\n\n" +
                             "Course Code: " + courseCode + "\n" +
                             "Name: " + selectedCourse.getName();
                           
        email.sendEmail(
            ((AuthenticatedUser) sharedContext.currentUser).getEmail(),
            SharedContext.ADMIN_STAFF_EMAIL,
            emailSubject,
            emailContent
        );
        
        view.displaySuccess("Course " + courseCode + " has been successfully removed.");
    }
    
    private void viewAllCourses() {
        if (sharedContext.getCourses().isEmpty()) {
            view.displayWarning("There are no courses in the system.");
            return;
        }
        
        view.displayInfo("=== All Courses ===");
        for (Course course : sharedContext.getCourses()) {
            view.displayInfo(
                course.getCourseCode() + " - " + course.getName() + "\n" +
                "Description: " + course.getDescription() + "\n" +
                "Organiser: " + course.getCourseOrganiserName() + "\n" +
                "Required Tutorials: " + course.getRequiredTutorials() + ", Labs: " + course.getRequiredLabs() + "\n"
            );
            view.displayDivider();
        }
        
        view.getInput("Press Enter to continue...");
    }
}
