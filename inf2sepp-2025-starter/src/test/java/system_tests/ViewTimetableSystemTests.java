package system_tests;

import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static system_tests.IntegrationTestCommon.*;

public class ViewTimetableSystemTests extends TUITest {

    @Test
    public void viewStudentTimetableAfterAddingCourse() throws URISyntaxException, IOException, ParseException {
        // Set up the context and login as admin to add a course
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Compose input stream to simulate user actionssetMockInput(
        setMockInput(concatUserInputs(
                addTestCourse1,
                logout,
                loginAsStudent,
                addTestCourseToTimetable,
                new String[] {
                        "5",       // MANAGE_TIMETABLE
                        "3",       // Choose Activity
                        "TEST111", // Course code
                        "1",       // Activity ID (Lecture)
                        "CHOSEN",  // Status
                        "2",       // View timetable
                        "4"        // Back to main menu
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

        // Verify timetable contains expected information
        assertOutputContains("activityType = LECTURE");
        assertOutputContains("day = MONDAY");
        assertOutputContains("startTime = 06:00");
        assertOutputContains("endTime = 07:00");
        assertOutputContains("courseCode = 'TEST111'");
        assertOutputContains("status = 'CHOSEN'");
    }
}
