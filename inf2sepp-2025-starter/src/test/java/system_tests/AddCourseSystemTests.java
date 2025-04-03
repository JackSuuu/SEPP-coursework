package system_tests;

import controller.AdminStaffController;
import controller.GuestController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class AddCourseSystemTests extends TUITest {

    private SharedContext context;

    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();
        setMockInput("admin1", "admin1pass");
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        guestController.login();
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());
    }

    private void setCourseInputWithExit(String... inputs) {
        String[] fullInputs = new String[inputs.length + 2];
        System.arraycopy(inputs, 0, fullInputs, 0, inputs.length);
        fullInputs[inputs.length] = "-1";  // exit course menu
        fullInputs[inputs.length + 1] = "-1";  // exit main menu
        setMockInput(fullInputs);
    }

    private void addSampleCourse() throws URISyntaxException, IOException, ParseException {
        setCourseInputWithExit(
                "0", "INFR0901", "Intro to Informatics",
                "Teaching the fundamentals of Informatics", "y",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "amieddm@hindeburg.ac.uk",
                "4", "4"
        );
        new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService()).manageCourse();
    }

    @Test
    public void testAddCourseSuccessfully() throws URISyntaxException, IOException, ParseException {
        setCourseInputWithExit(
                "0", "INFR0901", "Intro to Informatics",
                "Teaching the fundamentals of Informatics", "y",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "ami@uni.ac.uk",
                "4", "4"
        );
        AdminStaffController admin = new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageCourse();
        assertOutputContains("Course added successfully");
        assertOutputContains("Email from admin1@hindeburg.ac.uk to teacher1@hindeburg.ac.uk");
        assertOutputContains("Subject: Intro to Informatics");
        assertOutputContains("Description: Teaching the fundamentals of Informatics");
    }

    @Test
    public void testEmptyCourseCodeThenRecovery() throws URISyntaxException, IOException, ParseException {
        setCourseInputWithExit(
                "0", "", "INFR0902", "Intro to Testing", "Core concepts", "n",
                "Kiki", "kiki@uni.com", "Ami", "ami@uni.com", "2", "2"
        );
        AdminStaffController admin = new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageCourse();
        assertOutputContains("Course code cannot be empty.");
        assertOutputContains("Course added successfully.");
    }

    @Test
    public void testNonNumericTutorials() throws URISyntaxException, IOException, ParseException {
        setCourseInputWithExit(
                "0", "INFR0903", "Advanced Topics", "Cool stuff", "n",
                "Kiki", "kiki@uni.com", "Ami", "ami@uni.com", "abc", "3", "3"
        );
        AdminStaffController admin = new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageCourse();
        assertOutputContains("Invalid number. Please enter a valid integer for Tutorials.");
    }

    @Test
    public void testInvalidOrganiserEmail() throws URISyntaxException, IOException, ParseException {
        setCourseInputWithExit(
                "0", "INFR0904", "Bad Email Course", "Oops!", "n",
                "Kiara", "invalid-email", "Ami", "ami@uni.com", "3", "3"
        );
        AdminStaffController admin = new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageCourse();
        assertOutputContains("Course added successfully");
    }

    @Test
    public void testDuplicateCourseCode() throws URISyntaxException, IOException, ParseException {
        addSampleCourse();
        setCourseInputWithExit(
                "0", "INFR0901", "Something Else", "Duplicate!", "n",
                "Kiara", "kiara@uni.com", "Ami", "ami@uni.com", "1", "1"
        );
        AdminStaffController admin = new AdminStaffController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        admin.manageCourse();
        assertOutputContains("Course added successfully."); // change depending on how your system handles duplicates
    }
}
