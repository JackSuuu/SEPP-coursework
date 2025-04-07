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
import static system_tests.IntegrationTestCommon.exit;

public class ChooseTutorialOrLabSystemTests extends TUITest
{
    @Test
    public void mainSuccessScenario() throws URISyntaxException, IOException, ParseException {
        //login as admin, add courses.
        TUITest tui = new TUITest(); //provided helper test code.

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        tui.loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        tui.setMockInput(
                concatUserInputs(addTestCourseWithActivities,
                        addTestCourse2,
                        new String[]{"4"}, //view all courses
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        tui.startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        //tui.assertOutputContains("startTime = 06:00");
        tui.assertOutputContains("TEST111");
        tui.assertOutputContains("activityTypeLECTURE");
        tui.assertOutputContains("CHOSEN");


    }
}
