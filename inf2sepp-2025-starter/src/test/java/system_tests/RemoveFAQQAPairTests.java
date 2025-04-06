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

public class RemoveFAQQAPairTests extends TUITest {

    //*
    // Add FAQ Q-A System Tests
    // chicken jockeyyyy *//

    //*
    // Test: Add FAQ to New Topic (as Admin Staff)
    // *//
    @Test
    public void testAddFAQToNewTopic() throws URISyntaxException, IOException, ParseException {

        TUITest tui = new TUITest(); //provided helper test code.

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        tui.loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());


        // Step 2: Set inputs to add a FAQ to a new topic
        setMockInput(
                "2", "-2",                    // Select: Add FAQ item
                "New Topic",             // Input: Topic name
                "What is SEPP?",         // Input: FAQ question
                "SEPP is a course." ,     // Input: FAQ answer
                "0",                      // Select the new topic we just added
                "-3",               // Select "Remove FAQ item"
                "0",                 // Select the added FAQ QA pair
                "-1", "-1", "-1"           // Return to FAQ main menu + return to system main menu + exit
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.

        tui.startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        tui.assertOutputContains("Created topic 'New Topic'");
        tui.assertOutputContains("Email from admin1@hindeburg.ac.uk to inquiries@hindeburg.ac.nz");
        tui.assertOutputContains("Subject: FAQ topic 'New Topic' updated");
        tui.assertOutputContains("Description: Updated Q&As:");
        tui.assertOutputContains("Q: What is SEPP?");
        tui.assertOutputContains("A: SEPP is a course.");
        tui.assertOutputContains("Created new FAQ item");
        tui.assertOutputContains("FAQ item removed successfully.");
        tui.assertOutputContains("Topic 'New Topic' removed as it contained no FAQ items. Its subtopics were moved up one level.");
        
    }

}