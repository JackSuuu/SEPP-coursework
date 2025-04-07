package system_tests;

import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static system_tests.IntegrationTestCommon.*;

class RemoveCourseSystemTests extends TUITest {

    @Test
    void removeCourseAfterAddingCourse() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                addTestCourseWithActivities,
                new String[]{
                        "3", "1", "TEST111",
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

        assertOutputContains("Course removed successfully.");
    }

    @Test
    void removeNonExistentCourse() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[]{
                        "3", // Manage Courses
                        "1", // Remove course
                        "NONEXIST123", // Invalid code
                        "-1"
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

        assertOutputContains("Course not found.");
    }

    @Test
    void removeCourseWithoutAddingAny() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                new String[]{
                        "3", // Manage Courses
                        "1", // Remove course
                        "TEST999", // Random course code
                        "-1"
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

        assertOutputContains("Course not found.");
    }

    @Test
    void removeCourseTwice() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(concatUserInputs(loginAsAdmin,
                addTestCourse1,
                new String[]{
                        "3", "1", "TEST111",
                        "1", "TEST111", // try again
                        "-1"
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

        assertOutputContains("Course removed successfully.");
        assertOutputContains("Course not found.");
    }
}
