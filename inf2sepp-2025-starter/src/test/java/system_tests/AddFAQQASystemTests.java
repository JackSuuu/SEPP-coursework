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

    /*
     Add FAQ Q-A System Tests
    */

    //*
    // Test: Add FAQ to New Topic (as Admin Staff)
    // *//
    @Test
    public void testAddFAQToNewTopic() throws URISyntaxException, IOException, ParseException { //relates to R14b

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());


        // Step 2: Set inputs to add a FAQ to a new topic
        setMockInput(
                "2", "-2",                    // Select: Add FAQ item
                "New Topic",             // Input: Topic name
                "What is SEPP?",         // Input: FAQ question
                "SEPP is a course.",      // Input: FAQ answer
                "-1",                       //return to main menu
                "-1"                        //exit
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Created topic 'New Topic'");


    }



}