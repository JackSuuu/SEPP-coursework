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

class ConsultFAQSystemTests extends TUITest {

    @Test
    void mainSuccessScenario() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        // Simulate user flow:
        // - Admin logs in, adds an FAQ
        // - Logs out
        // - Guest logs in and consults FAQ
        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        addFAQItem,
                        logout,
                        new String[] {
                                "1", // CONSULT_FAQ
                                "0", // Select section (New Topic)
                                "-1", // Exit section
                                "-1", // Exit FAQ
                                "-1"  // Fully exit the system
                        }
                )
        );


        startOutputCapture();
        MenuController menus = new MenuController(
                context,
                new TextUserInterface(),
                new MockAuthenticationService(),
                new MockEmailService()
        );

        menus.mainMenu();

        assertOutputContains("What is SEPP?");
        assertOutputContains("> SEPP is a course");
    }

    @Test
    void testConsultFAQInvalidIndex() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        addFAQItem,
                        logout,
                        new String[] {
                                "1",   // CONSULT_FAQ
                                "5",   // Invalid topic index
                                "-1",  // Exit FAQ
                                "-1"   // Exit system
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Invalid option: 5");
        assertOutputContains("What is SEPP?");
    }

}
