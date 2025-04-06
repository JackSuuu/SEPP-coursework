package system_tests;

import controller.GuestController;
import external.MockAuthenticationService;
import external.MockEmailService;
import model.AuthenticatedUser;
import model.SharedContext;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import view.TextUserInterface;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class ViewCourseSystemTests {

    private SharedContext context;
    @BeforeEach
    public void setUp() throws URISyntaxException, IOException, ParseException {
        context = new SharedContext();

        // Simulates: username, password, and selection of menu option 3 (Manage Courses)
        //setMockInput("admin1", "admin1pass", "3");

        GuestController guestController = new GuestController(context, new TextUserInterface(),
                new MockAuthenticationService(), new MockEmailService());
        guestController.login();

        // Ensure the user is authenticated and is of AdminStaff role
        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());
    }



}
