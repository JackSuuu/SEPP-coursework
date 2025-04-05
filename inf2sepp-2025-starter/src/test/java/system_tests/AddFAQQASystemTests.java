package system_tests;

import controller.AdminStaffController;
import controller.GuestController;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AddFAQQASystemTests extends TUITest {

    //*
    // Add FAQ Q-A System Tests
    // chicken jockeyyyy *//

    //*
    // Test: Add FAQ to New Topic (as Admin Staff)
    // *//
    @Test
    public void testAddFAQToNewTopic() throws URISyntaxException, IOException, ParseException {

        TUITest tui = new TUITest();

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();



        //GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        tui.loginAsAdminStaff(context);
        //System.out.println( ((AuthenticatedUser) context.currentUser).getRole());

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());


        //System.out.println(context.getFaqManager().getRootSections());

        // Step 3: Add a FAQ to a new topic
        setMockInput(
                "2", "-2",                    // Select: Add FAQ item
                "New Topic",             // Input: Topic name
                "What is SEPP?",         // Input: FAQ question
                "SEPP is a course.",      // Input: FAQ answer
                "-1",
                "-1"
        );

        // Step 3: Verify the outputs

        tui.startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        tui.assertOutputContains("Created topic \'New Topic\'");
    }

}