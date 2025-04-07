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

public class ConsultMemberOfStaffTests extends TUITest {

    @Test
    public void testSubmitValidInquiry() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                "2",                           // CONTACT_STAFF
                "student1@hindeburg.ac.uk",    // email
                "Food Support",                // subject
                "Can I get instant ramen?",    // body
                "-1"                           // exit
        );

        startOutputCapture();
        new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService())
                .mainMenu();

        assertOutputContains("Your inquiry has been recorded. Someone will be in touch via email soon!");
        assertOutputContains("Email from inquiries@hindeburg.ac.nz to inquiries@hindeburg.ac.nz");
    }

    @Test
    public void testEmptyInquirySubject() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                "2",
                "student1@hindeburg.ac.uk",
                "",                            // empty subject
                "I have a question!",
                "-1"
        );

        startOutputCapture();
        new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService())
                .mainMenu();

        assertOutputContains("Inquiry subject cannot be blank! Please try again");
    }

    @Test
    public void testEmptyInquiryBody() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                "2",
                "student1@hindeburg.ac.uk",
                "Exam Schedule",
                "",                            // empty message
                "-1"
        );

        startOutputCapture();
        new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService())
                .mainMenu();

        assertOutputContains("Inquiry content cannot be blank! Please try again");
    }

    @Test
    public void testInvalidEmailFormat() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                "2",
                "not-an-email",               // invalid email
                "Grades",
                "Where are my grades?",
                "-1"
        );

        startOutputCapture();
        new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService())
                .mainMenu();

        assertOutputContains("Invalid email address! Please try again");
    }

    @Test
    public void testGuestCanExitWithoutInquiry() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                "-1"
        );

        startOutputCapture();
        new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService())
                .mainMenu();

        assertOutputContains("Hello! What would you like to do?");
    }
}
