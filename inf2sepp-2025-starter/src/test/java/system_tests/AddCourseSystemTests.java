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

/**
 * This class tests the "Manage Courses" functionality available to AdminStaff users.
 * It simulates full user input flows (as if navigating through the terminal menu).
 */
public class AddCourseSystemTests extends TUITest {

    private SharedContext context;

    /**
     * Sets up the test context by logging in as an AdminStaff user
     * and simulating selection of "Manage Courses" from the main menu.
     *
     * Although "3" is pushed as input (indicating the Manage Courses menu),
     * we must still manually call `manageCourse()` since no actual main menu dispatcher is present.
     */
    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();

        // Simulates: username, password, and selection of menu option 3 (Manage Courses)
        setMockInput("admin1", "admin1pass", "3");

        GuestController guestController = new GuestController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        guestController.login();

        // Ensure the user is authenticated and is of AdminStaff role
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());
    }

    /**
     * Helper method to push course-related inputs and enter course management.
     * This simulates full user interaction inside the "Manage Courses" submenu.
     *
     * @param inputs User inputs to simulate within the course management screen.
     */
    private void runAdminMenuWithInput(String... inputs) throws URISyntaxException, IOException, ParseException {
        String[] fullInputs = new String[inputs.length + 1];
        System.arraycopy(inputs, 0, fullInputs, 0, inputs.length);
        fullInputs[inputs.length] = "-1"; // Exit course menu

        setMockInput(fullInputs);

        AdminStaffController admin = new AdminStaffController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );

        // Capture output before controller runs
        startOutputCapture();

        admin.manageCourse(); // must happen AFTER capture starts
    }

    /**
     * Validates that a course is added correctly with valid inputs.
     */
    @Test
    public void testAddCourseSuccessfully() throws URISyntaxException, IOException, ParseException {
        runAdminMenuWithInput(
                "0", "INFR0901", "Intro to Informatics",
                "Teaching the fundamentals of Informatics", "y",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "ami@uni.ac.uk",
                "4", "4"
        );
        assertOutputContains("Course added successfully");
        assertOutputContains("Email from admin1@hindeburg.ac.uk to teacher1@hindeburg.ac.uk");
        assertOutputContains("Subject: Intro to Informatics");
        assertOutputContains("Description: Teaching the fundamentals of Informatics");
    }

    /**
     * Ensures that empty course code is rejected,
     * and that the user can retry with valid input afterward.
     */
    @Test
    public void testEmptyCourseCodeThenRecovery() throws URISyntaxException, IOException, ParseException {
        runAdminMenuWithInput(
                "0", "", "INFR0902", "Intro to Testing",
                "Core concepts", "n",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "teacher5@hindeburg.ac.uk", "2", "2"
        );
        assertOutputContains("Course code cannot be empty.");
        assertOutputContains("Course added successfully.");
    }

    /**
     * Verifies the system rejects non-numeric values for tutorial count.
     */
    @Test
    public void testNonNumericTutorials() throws URISyntaxException, IOException, ParseException {
        runAdminMenuWithInput(
                "0", "INFR0903", "Advanced Topics", "Cool stuff", "n",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "teacher5@hindeburg.ac.uk", "abc", "3", "3"
        );
        assertOutputContains("Invalid number. Please enter a valid integer for Tutorials.");
    }

    /**
     * Tests that organiser email format is not enforced in current implementation,
     * and course still proceeds as successfully added.
     */
    @Test
    public void testInvalidOrganiserEmail() throws URISyntaxException, IOException, ParseException {
        runAdminMenuWithInput(
                "0", "INFR0904", "Bad Email Course", "Oops!", "n",
                "Miss Teachings", "invalid-email",
                "Amie Montagne", "teacher5@hindeburg.ac.uk", "3", "3"
        );
        assertOutputContains("Course added successfully");
    }

    /**
     * Ensures that if the same course code is added twice,
     * system accepts the second addition unless duplicate checking is implemented.
     */
    @Test
    public void testDuplicateCourseCode() throws URISyntaxException, IOException, ParseException {
        runAdminMenuWithInput(
                "0", "INFR0901", "Intro to Informatics", "Original", "y",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "teacher5@hindeburg.ac.uk", "3", "3"
        );

        runAdminMenuWithInput(
                "0", "INFR0901", "Something Else", "Duplicate!", "n",
                "Kiara Havrezeinn", "teacher1@hindeburg.ac.uk",
                "Amie Montagne", "teacher5@hindeburg.ac.uk", "1", "1"
        );

        assertOutputContains("Course added successfully");
    }
}
