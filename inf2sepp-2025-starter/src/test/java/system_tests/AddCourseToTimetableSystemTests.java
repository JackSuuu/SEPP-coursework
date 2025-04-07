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

class AddCourseToTimetableSystemTests extends TUITest
{

    @Test
    void testAddCourseToTimetable() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(
                concatUserInputs(loginAsAdmin,
                        addTestCourseWithActivities,
                        logout,
                        loginAsStudent,
                        addTestCourseToTimetable,
                        exit)
        );

        // Step 3: generate menu controller, feed it these inputs and assert the output succeeds.
        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Course TEST111 was successfully added to your timetable");

    }

    @Test
    void testAddMultipleCourses() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        addTestCourseWithActivities,
                        addTestCourse2,
                        logout,
                        loginAsStudent,
                        new String[] {
                                "5", "0", "TEST111", "0", "TEST222", "4"
                        },
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Course TEST111 was successfully added to your timetable");
        assertOutputContains("Course TEST222 was successfully added to your timetable");

    }

    @Test
    void addSameCourseToTimetable() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        addTestCourseWithActivities,
                        logout,
                        loginAsStudent,
                        new String[] {
                                "5", "0", "TEST111", "0", "TEST111", "4"
                        },
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Course TEST111 was successfully added to your timetable");

    }

    @Test
    void addNonexistentCourse() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();
        setMockInput(
                concatUserInputs(
                        loginAsStudent,
                        new String[] {
                                "5", "0", "TEST111", "4"
                        },
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Incorrect course code");
        assertOutputContains("Course TEST111 add fail");
    }


}
