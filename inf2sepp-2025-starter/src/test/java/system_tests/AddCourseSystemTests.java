package system_tests;

import controller.AdminStaffController;
import controller.GuestController;
import controller.MenuController;
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
import static system_tests.IntegrationTestCommon.*;


/**
 * This class tests the "Manage Courses" functionality available to AdminStaff users.
 * It simulates full user input flows (as if navigating through the terminal menu).
 */
public class AddCourseSystemTests extends TUITest {

    /**
     * Validates that a course is added correctly with valid inputs.
     */
    @Test
    public void testAddCourseSuccessfully() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[] {
                        "3", // MANAGE_COURSES
                        "0", // ADD_COURSES
                        "TEST111",
                        "Test Course",
                        "Integration test course",
                        "y",
                        "Tester",
                        "test@testsite",
                        "testsec",
                        "testsec@testsite",
                        "12",
                        "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();

        assertOutputContains("Course added successfully");
    }


    /**
     * Ensures that empty course code is rejected,
     * and that the user can retry with valid input afterward.
     */
    @Test
    public void testEmptyCourseCodeThenRecovery() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[] {
                        "3", "0",
                        "",    // Empty course code
                        "TEST111", "Test Course", "Integration test course", "y", "Tester",
                        "test@testsite", "testsec", "testsec@testsite", "12", "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();

        assertOutputContains("Course code cannot be empty.");
    }

    /**
     * Verifies the system rejects non-numeric values for tutorial count.
     */
    @Test
    public void testNonNumericTutorials() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[] {
                        "3", "0", "TEST111", "Test Course", "Integration test course",
                        "y", "Tester", "test@testsite", "testsec", "testsec@testsite",
                        "abc", // non-numeric tutorials
                        "12", "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();

        assertOutputContains("Invalid number. Please enter a valid integer for Tutorials.");
    }

    /**
     * Verifies the system rejects non-numeric values for lab count.
     */
    @Test
    public void testNonNumericLabs() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[] {
                        "3", "0", "TEST111", "Test Course", "Integration test course", "y",
                        "Tester", "test@testsite", "testsec", "testsec@testsite", "12",
                        "abc", // non-numeric labs
                        "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();

        assertOutputContains("Invalid number. Please enter a valid integer for Labs.");
    }

    /**
     * Tests that organiser email format is not enforced in current implementation,
     * and course still proceeds as successfully added.
     */
    @Test
    public void testInvalidOrganiserEmail() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[] {
                        "3", "0", "TEST111", "Test Course", "Integration test course", "y", "Tester",
                        "bad-email", // invalid organiser email
                        "testsec", "testsec@testsite", "12", "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();
        assertOutputContains("Course added successfully.");
    }

    /**
     * Tests that organiser email format is not enforced in current implementation,
     * and course still proceeds as successfully added.
     */
    @Test
    public void testInvalidSecretaryEmail() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[] {
                        "3", "0", "TEST111", "Test Course", "Integration test course", "y",    //requires computer
                        "Tester", "test@testsite", "testsec", "bad-email-2", "12",
                        "abc", // non-numeric labs
                        "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();
        assertOutputContains("Course added successfully.");
    }

    /**
     * Ensures that if the same course code is added twice,
     * system accepts the second addition unless duplicate checking is implemented.
     */
    @Test
    public void testDuplicateCourseCode() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[]{
                        "3", "0", "TEST111", "Test Course", "Integration test course", "y",
                        "Tester", "test@testsite", "testsec", "testsec@testsite", "12", "4",
                },
                new String[]{   // duplicate of above
                        "0", "TEST111", "Test Course", "Integration test course", "y",
                        "Tester", "test@testsite", "testsec", "testsec@testsite", "12", "4",
                        "-1" // main menu
                },
                exit
        ));

        startOutputCapture();
        MenuController controller = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );
        controller.mainMenu();

        assertOutputContains("Course added successfully");
    }
}
