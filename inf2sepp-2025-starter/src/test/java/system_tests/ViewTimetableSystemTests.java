package system_tests;

import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static system_tests.IntegrationTestCommon.*;

class ViewTimetableSystemTests extends TUITest {

    @Test
    void viewStudentTimetableAfterAddingCourse() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        setMockInput(concatUserInputs(
                addTestCourseWithActivities,
                logout,
                loginAsStudent,
                addTestCourseToTimetable,
                new String[] {
                        "5", "3", "TEST111", "1", "CHOSEN", "2", "4"
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

        assertOutputContains("activityType = LECTURE");
        assertOutputContains("day = MONDAY");
        assertOutputContains("startTime = 06:00");
        assertOutputContains("endTime = 07:00");
        assertOutputContains("courseCode = 'TEST111'");
        assertOutputContains("status = 'CHOSEN'");
    }

    @Test
    void viewEmptyTimetableShowsNotice() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsStudent(context);

        setMockInput(concatUserInputs(
                new String[] { "5", "2", "4" }, // View timetable, Back
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

        assertOutputContains("Student timetable not yet created");
    }

    @Test
    void viewTimetableWithUnchosenStatus() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        setMockInput(concatUserInputs(
                addTestCourseWithActivities,
                logout,
                loginAsStudent,
                addTestCourseToTimetable,
                new String[] {
                        "5", "3", "TEST111", "1", "UNCHOSEN", "2", "4"
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

        assertOutputContains("status = 'UNCHOSEN'");
    }
}
