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

class AddCourseToTimetableSystemTests extends TUITest
{

    @Test
    void addCourseToTTAndViewAsStudent() throws URISyntaxException, IOException, ParseException {
        // TODO: Shouldn't the testcases here be only for creating the timetable? Not viewing it?
        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        // Step 2: Set inputs to add a new course
        setMockInput(
                concatUserInputs(loginAsAdmin,
                        addTestCourseWithActivities,
                        new String[]{"4"}, //view all courses
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Activity added successfully.");
        assertOutputContains("TEST111");




    }

}
