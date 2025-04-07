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


class ViewCoursesSystemTests extends TUITest {

    @Test
    void viewCourseAfterAdding() throws IOException, URISyntaxException, ParseException {

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        setMockInput(
                concatUserInputs(addTestCourseWithActivities,
                        new String[]{"3","4"}, //view all courses,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("courseCode = 'TEST111',\n" +
                " name = 'Test Course',\n" +
                " description = 'Integration test course',\n" +
                " requiresComputers = true,\n" +
                " courseOrganiserName = 'Tester',\n" +
                " courseOrganiserEmail = 'test@testsite',\n" +
                " courseSecretaryName = 'testsec',\n" +
                " courseSecretaryEmail = 'testsec@testsite',\n" +
                " requiredTutorials = 12,\n" +
                " requiredLabs = 4");
    }


    @Test
    void viewMultipleCoursesAfterAdding() throws URISyntaxException, IOException, ParseException {

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        setMockInput(
                concatUserInputs(addTestCourseWithActivities, addTestCourse2,
                        new String[]{"3","4"}, //view all courses,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("courseCode = 'TEST111',\n" +
                " name = 'Test Course',\n" +
                " description = 'Integration test course',\n" +
                " requiresComputers = true,\n" +
                " courseOrganiserName = 'Tester',\n" +
                " courseOrganiserEmail = 'test@testsite',\n" +
                " courseSecretaryName = 'testsec',\n" +
                " courseSecretaryEmail = 'testsec@testsite',\n" +
                " requiredTutorials = 12,\n" +
                " requiredLabs = 4");

        assertOutputContains("courseCode = 'TEST222',\n" +
                " name = 'Test Course 2',\n" +
                " description = 'Integration test course 2',\n" +
                " requiresComputers = false,\n" +
                " courseOrganiserName = 'OtherTester',\n" +
                " courseOrganiserEmail = 'test2@testsite',\n" +
                " courseSecretaryName = 'othertestsec',\n" +
                " courseSecretaryEmail = 'testsec2@testsite',\n" +
                " requiredTutorials = 120,\n" +
                " requiredLabs = 7");
    }


    @Test
    void viewNoCourses() throws URISyntaxException, IOException, ParseException {

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();
        loginAsAdminStaff(context);

        assertInstanceOf(AuthenticatedUser.class, context.currentUser);
        assertEquals("AdminStaff", ((AuthenticatedUser) context.currentUser).getRole());

        // Step 2: Set inputs to add a new course
        setMockInput(
                concatUserInputs(new String[]{"3","4"}, //view all courses,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("No courses are available in the system right now.");
    }



}
