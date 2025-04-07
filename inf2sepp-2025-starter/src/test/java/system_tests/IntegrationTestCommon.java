package system_tests;

import java.util.stream.Stream;

public class IntegrationTestCommon
{
    public static final String[] addTestCourse1 = {
            //add a test course
            "3",
            "0",
            "TEST111",
            "Test Course",
            "Integration test course",
            "y",    //requires computer
            "Tester",
            "test@testsite",
            "testsec",
            "testsec@testsite",
            "12", //num tuts
            "4", //num labs

            //add a lecture
            "2", //add activity
            "TEST111",
            "1", //lecture
            "2025-01-01",
            "06:00:00",
            "2025-01-01",
            "07:00:00",
            "Testvenue",
            "MONDAY",
            "LECTURE",
            "y", //recording enabled
            "-1", //return to main menu

            //add a tutorial
            "2", "TEST111",
            "2", //add tutorial
            "2025-01-01",
            "07:00:00",
            "2025-01-01",
            "08:00:00",
            "tutvenue",
            "MONDAY",
            "TUTORIAL",
            "40", //capacity
            "-1" //return to main menu

    };

    public static final String[] logout = {
            "0"
    };

    public static final String[] loginAsStudent = {
            "0",
            "student1",
            "student1pass"
    };

    public static final String[] addTestCourseToTimetable = {
            "5",
            "0",
            "TEST111", //add to timetable
            "3", //choose activity
            "TEST111",
            "1", //lecture
            "CHOSEN",
            "2", //view timetable
            "4" //return to main menu
    };


    public static final String[] exit = {
            "-1", "-1" //exit
    };

    public static final String[] addTestCourse2 = {
            //add a test course
            "3",
            "0",
            "TEST222",
            "Test Course 2",
            "Integration test course 2",
            "n",    //requires computer
            "OtherTester",
            "test2@testsite",
            "othertestsec",
            "testsec2@testsite",
            "120", //num tuts
            "7", //num labs

            //add a lecture
            "2", //add activity
            "TEST222",
            "1", //lecture
            "2025-01-01",
            "06:00:00",
            "2025-01-01",
            "07:00:00",
            "Testvenue",
            "MONDAY",
            "LECTURE",
            "n", //recording enabled
            "-1" //return to main menu
    };

    public static final String[] addFAQItem = {
            "2", "-2",                    // Select: Add FAQ item
            "New Topic",             // Input: Topic name
            "What is SEPP?",         // Input: FAQ question
            "SEPP is a course.",      // Input: FAQ answer
            "n",
            "-1"                       //return to main menu
    };


    //flatten and concatenate an arbitrary number of String arrays into one.
    public static String[] concatUserInputs(String[]... all){
        String[] out = Stream.of(all).flatMap(Stream::of).toArray(String[]::new);
        return out;
    }

}
