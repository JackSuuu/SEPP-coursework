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

public class AddFAQQASystemTests extends TUITest {

    /*
     Add FAQ Q-A System Tests
    */

    @Test
    public void testAddNewTopic() throws URISyntaxException, IOException, ParseException { //relates to R14b

        // Step 1: Log in as admin1
        SharedContext context = new SharedContext();


        // Step 2: Set inputs to add a FAQ to a new topic
        setMockInput(
                concatUserInputs(loginAsAdmin,
                new String[]{
                "2", "-2",                    // Select: Add FAQ item
                "New Topic",             // Input: Topic name
                "What is SEPP?",         // Input: FAQ question
                "SEPP is a course.",      // Input: FAQ answer
                "n",      // Input: FAQ answer
                "-1",                       //return to main menu
                "-1"}                        //exit
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
        assertOutputContains("The new FAQ item was added");


    }

    @Test
    public void testAddNewTopicWithoutCourses() throws URISyntaxException, IOException, ParseException { //relates to R14b

        SharedContext context = new SharedContext();

        // Step 2: Set inputs to add a FAQ to a new topic
        setMockInput(
                concatUserInputs(loginAsAdmin,
                        new String[]{
                                "2", "-2", "New Topic", "What is SEPP?", "SEPP is a course.",
                                "y", "TEST111", "TEST111", "-1"},
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Course 'TEST111' not found.");


    }

    @Test
    public void testAddNewTopicAfterCourses() throws URISyntaxException, IOException, ParseException { //relates to R14b

        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(loginAsAdmin,
                        addTestCourse1,
                        new String[]{
                                "2", "-2", "New Topic", "What is SEPP?", "SEPP is a course.",
                                "y", "TEST111", "TEST111", "-1"},
                        exit
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(),  new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Available courses: [Course -> {\n" +
                "courseCode = 'TEST111',\n" +
                " name = 'Test Course',\n" +
                " description = 'Integration test course',\n" +
                " requiresComputers = true,\n" +
                " courseOrganiserName = 'Tester',\n" +
                " courseOrganiserEmail = 'test@testsite',\n" +
                " courseSecretaryName = 'testsec',\n" +
                " courseSecretaryEmail = 'testsec@testsite',\n" +
                " requiredTutorials = 12,\n" +
                " requiredLabs = 4}\n" +
                "]");

        assertOutputContains("Created topic 'New Topic'");
        assertOutputContains("Email from admin1@hindeburg.ac.uk to inquiries@hindeburg.ac.nz");
        assertOutputContains("Subject: FAQ topic 'New Topic' updated");
        assertOutputContains("Description: Updated Q&As:");
        assertOutputContains("Q: What is SEPP?");
        assertOutputContains("A: SEPP is a course.");
        assertOutputContains("The new FAQ item was added");

    }

    @Test
    public void testAddFAQToExistingTopic() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        new String[]{
                                "2", "-2", "General Info", "What is SEPP?", "SEPP is a course.",
                                "n",  // do not associate with course
                                "2", "-2", "General Info", "Who teaches SEPP?", "Lecturers teach SEPP.",
                                "n",  // still no course link
                                "-1", "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Created topic 'General Info'");
        assertOutputContains("Q: What is SEPP?");
        assertOutputContains("A: SEPP is a course");
        assertOutputContains("Q: Who teaches SEPP?");
        assertOutputContains("A: Lecturers teach SEPP.");
    }

    @Test
    public void testAddFAQItemWithEmptyQuestion() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        new String[]{
                                "2", "-2", "Policy", "",
                                "Valid question.", "Valid answer.",
                                "n", "-1", "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("FAQ question cannot be empty.");
    }

    @Test
    public void testAddFAQItemWithEmptyAnswer() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        new String[]{
                                "2", "-2", "Policy", "Valid question.", "",
                                "Valid answer.", "n", "-1", "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("FAQ answer cannot be empty.");
    }

    @Test
    public void testCancelFAQCreation() throws URISyntaxException, IOException, ParseException {
        SharedContext context = new SharedContext();

        setMockInput(
                concatUserInputs(
                        loginAsAdmin,
                        new String[]{
                                "2", "-1", // Exit from FAQ menu
                                "-1"
                        }
                )
        );

        startOutputCapture();
        MenuController menus = new MenuController(context, new TextUserInterface(), new MockAuthenticationService(), new MockEmailService());
        menus.mainMenu();

        assertOutputContains("Return to main menu");
    }






}