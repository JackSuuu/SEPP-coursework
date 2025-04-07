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

public class ConsultFAQSystemTests extends TUITest{

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
                concatUserInputs(addFAQItem,
                        new String[]{"0"}, //view all courses
                        logout,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        tui.startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        //tui.assertOutputContains("startTime = 06:00");
        tui.assertOutputContains("What is SEPP?");
        tui.assertOutputContains("SEPP is a course");
        tui.assertOutputContains("CHOSEN");


    }
}
