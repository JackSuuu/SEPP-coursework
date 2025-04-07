package system_tests;

import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static system_tests.IntegrationTestCommon.*;

public class RemoveFAQQAPairTests extends TUITest {

    @Test
    public void testRemoveFAQAfterAdding() throws URISyntaxException, IOException, ParseException {

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();


        // Step 2: Set inputs to add a FAQ to a new topic
        setMockInput(
                concatUserInputs(loginAsAdmin,
                        new String[] {
                                "2", "-2",                    // Select: Add FAQ item
                                "New Topic",             // Input: Topic name
                                "What is SEPP?",         // Input: FAQ question
                                "SEPP is a course." ,     // Input: FAQ answer
                                "n",     // Input: FAQ answer
                                "0",                      // Select the new topic we just added
                                "-3",               // Select "Remove FAQ item"
                                "0",                 // Select the added FAQ QA pair
                                "-1", "-1", "-1"           // Return to FAQ main menu + return to system main menu + exit
                        }
                )
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Created topic 'New Topic'");
        assertOutputContains("Email from admin1@hindeburg.ac.uk to inquiries@hindeburg.ac.nz");
        assertOutputContains("Subject: FAQ topic 'New Topic' updated");
        assertOutputContains("Description: Updated Q&As:");
        assertOutputContains("Q: What is SEPP?");
        assertOutputContains("A: SEPP is a course.");
        assertOutputContains("FAQ item removed successfully.");
        assertOutputContains("Topic 'New Topic' removed as it contained no FAQ items. Its subtopics were moved up one level.");
        
    }

    @Test
    public void testRemoveFAQFromEmptyTopic() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(loginAsAdmin,
                        new String[] {
                                "2", "-2", "EmptyTopic", "Q?", "A.", "n",  // Add then remove
                                "0", "-3", "0",                             // Remove the only item
                                "-2", "EmptyTopic", "Q2?", "A2.", "n",      // Add again
                                "0", "-3", "0",                             // Remove again
                                "-1", "-1", "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("FAQ item removed successfully.");
        assertOutputContains("Topic 'EmptyTopic' removed as it contained no FAQ items.");
    }

    @Test
    public void testRemoveFAQWithInvalidIndex() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(loginAsAdmin,
                        new String[] {
                                "2", "-2", "TopicX", "Is this real?", "Yes", "n",
                                "0", "-3", "999", // Invalid index
                                "-1", "-1", "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Invalid index");
    }

    @Test
    public void testRemoveFAQWithNonNumericInput() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(loginAsAdmin,
                        new String[] {
                                "2", "-2", "TopicY", "What now?", "This.", "n",
                                "0", "-3", "oops",  // Non-numeric input
                                "-1", "-1", "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Invalid input. Please enter a valid number.");
    }




}