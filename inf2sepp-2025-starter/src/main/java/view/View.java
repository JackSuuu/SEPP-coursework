package view;

import model.FAQManager;
import model.FAQSection;
import model.Inquiry;
import model.Course;
import model.Timetable;

public interface View {
    String getInput(String prompt);
    boolean getYesNoInput(String prompt);
    void displayInfo(String text);
    void displaySuccess(String text);
    void displayWarning(String text);
    void displayError(String text);
    void displayException(Exception e);
    void displayDivider();
    void displayFAQ(FAQManager faq);
    void displayFAQSection(FAQSection section);
    void displayInquiry(Inquiry inquiry);
    void displayCourse(Course course);
    void displayTimetable(Timetable timetable);
}
