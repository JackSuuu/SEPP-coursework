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

class ConsultFAQSystemTests extends TUITest{

    @Test
    void mainSuccessScenario() throws URISyntaxException, IOException, ParseException {
        //login as admin, add courses.

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        setMockInput(
                concatUserInputs(addFAQItem,
                        new String[]{"0"}, //view all courses
                        logout,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        //tui.assertOutputContains("startTime = 06:00");
        assertOutputContains("What is SEPP?");
        assertOutputContains("SEPP is a course");
        assertOutputContains("CHOSEN");


    }
}
