package system_tests;

import controller.AdminStaffController;
import controller.GuestController;
import controller.MenuController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.Guest;
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

public class ConsultMemberOfStaffTests extends TUITest {

    //*
    // Add FAQ Q-A System Tests
    // chicken jockeyyyy *//

    //*
    // Test: Add FAQ to New Topic (as Admin Staff)
    // *//
    @Test
    public void testAddFAQToNewTopic() throws URISyntaxException, IOException, ParseException {

        TUITest tui = new TUITest(); //provided helper test code.

        // Step 1: Act as a guest
        SharedContext context = new SharedContext();

        // Step 2: Set inputs to consult a member of staff
        setMockInput(
                "2",                   // Select: CONTACT_STAFF
                "student1@hindeburg.ac.uk",           // Enter consult email
                "food allowance",                    //  Enter topic for inquiry
                "can I eat noodles",
                "-1"                                 // exit
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.

        tui.startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        tui.assertOutputContains("Email from inquiries@hindeburg.ac.nz to inquiries@hindeburg.ac.nz");
        tui.assertOutputContains("Subject: New inquiry from student1@hindeburg.ac.uk");
        tui.assertOutputContains("Description: Subject: food allowance");
        tui.assertOutputContains("Please log into the Self Service Portal to review and respond to the inquiry.");
        tui.assertOutputContains("Your inquiry has been recorded. Someone will be in touch via email soon!");
        
    }

}