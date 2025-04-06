package system_tests;

import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static system_tests.IntegrationTestCommon.*;

public class AddCourseToTimetableSystemTest extends TUITest
{

    TUITest testTUI;

    @Test
    public void setup() throws URISyntaxException, IOException, ParseException {
        //login as admin, add courses.
        TUITest tui = new TUITest(); //provided helper test code.

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        tui.loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        tui.setMockInput(
                concatStrings(addTestCourse1,
                        new String[]{"4"} //view all courses
                        ,exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        tui.startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        tui.assertOutputContains("TEST111");


    }

}
