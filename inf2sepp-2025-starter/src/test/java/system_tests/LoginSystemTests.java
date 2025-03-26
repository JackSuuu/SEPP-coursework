package system_tests;

import controller.GuestController;
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

public class LoginSystemTests extends TUITest {

    //*
    // Test logins for different roles (AdminStaff, TeachingStaff, Student)
    // *//

    @Test
    public void testLoginAsAdminStaff() throws URISyntaxException, IOException, ParseException {
        setMockInput("admin1", "admin1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        guestController.login();
        assertOutputContains("Logged in as admin1");
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());
    }

    @Test
    public void testLoginAsTeachingStaff() throws URISyntaxException, IOException, ParseException {
        setMockInput("teacher1", "teacher1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        guestController.login();
        assertOutputContains("Logged in as teacher1");
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("TeachingStaff", ((AuthenticatedUser) context.currentUser).getRole());
    }

    @Test
    public void testLoginAsStudent() throws URISyntaxException, IOException, ParseException {
        setMockInput("student1", "student1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        startOutputCapture();
        guestController.login();
        assertOutputContains("Logged in as student1");
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("Student", ((AuthenticatedUser) context.currentUser).getRole());
    }

    //*
    // Test incorrect passwords or usernames.
    // *//

    @Test
    public void testIncorrectPassword() throws URISyntaxException, IOException, ParseException {
        setMockInput("student1", "admin1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        guestController.login();

        assertOutputContains("Wrong username or password");
    }

    @Test
    public void testInvalidUsername() throws URISyntaxException, IOException, ParseException {
        setMockInput("22", "admin1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        guestController.login();

        assertOutputContains("Wrong username or password");
    }

    @Test
    public void testNullUsername() throws URISyntaxException, IOException, ParseException {
        setMockInput(null, "admin1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        guestController.login();

        assertOutputContains("Wrong username or password");
    }

    @Test
    public void testEmptyUsername() throws URISyntaxException, IOException, ParseException {
        setMockInput("", "admin1pass");
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        guestController.login();

        assertOutputContains("Wrong username or password");
    }

    //*
    // Test different capitalisations of usernames and/or passwords
    // *//

    @ParameterizedTest(name = "Login with username={0} and password={1} should fail")
    @CsvSource({
            "STUDENT1, student1pass",
            "Student1, student1pass",
            "student1, Student1PASS",
            "STUDENT1, STUDENT1PASS",
            "sTuDeNt1, sTuDeNt1pAsS"
    })
    public void testDifferentCaseUsernames(String username, String password) throws URISyntaxException, IOException, ParseException {
        setMockInput(username, password);
        SharedContext context = new SharedContext();
        GuestController guestController = new GuestController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());

        startOutputCapture();
        guestController.login();

        assertOutputContains("Wrong username or password");
    }


}