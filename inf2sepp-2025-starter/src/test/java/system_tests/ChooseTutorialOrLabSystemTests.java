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
import static system_tests.IntegrationTestCommon.exit;

class ChooseTutorialOrLabSystemTests extends TUITest
{
    @Test
    void mainSuccessScenario() throws URISyntaxException, IOException, ParseException {

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        setMockInput(
                concatUserInputs(addTestCourseWithActivities,
                        addTestCourse2,
                        new String[]{"4"}, //view all courses
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        exit)
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Activity set successfully for course TEST111");
        assertOutputContains("activityType = LECTURE\n" +
                ", activityId = 1\n" +
                ", day = MONDAY\n" +
                ", startTime = 06:00\n" +
                ", endTime = 07:00\n" +
                ", courseCode = 'TEST111'\n" +
                ", status = 'CHOSEN'");


    }

    @Test
    void chooseTutorialForAddedCourse() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        setMockInput(
                concatUserInputs(
                        addTestCourseWithActivities,
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        new String[]{
                                "5", "3", "TEST111", "2", "CHOSEN", // choose TUTORIAL
                                "2", "4" // view timetable, return
                        },
                        exit
                )
    );

    startOutputCapture();
    MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
    menus.mainMenu();

    assertOutputContains("Activity set successfully for course TEST111");
    assertOutputContains("activityType = TUTORIAL");
    assertOutputContains("status = 'CHOSEN'");
}

    @Test
    void chooseLabForAddedCourse() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        setMockInput(
                concatUserInputs(
                        addTestCourseWithActivities,
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        new String[]{
                                "5", "3", "TEST111", "3", "CHOSEN", // Choose LAB
                                "5", "2", "4"                  // View timetable, return
                        },
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Activity set successfully for course TEST111");
        assertOutputContains("activityType = LAB");
        assertOutputContains("status = 'CHOSEN'");
    }

    @Test
    void chooseInvalidActivityId() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        setMockInput(
                concatUserInputs(
                        addTestCourseWithActivities,
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        new String[]{
                                "5", "3", "TEST111", "99", "CHOSEN", // Invalid ID
                                "5", "2", "4"
                        },
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Invalid activity id or status");
        assertOutputContains("Failed to set activity for course TEST111");
    }

}
