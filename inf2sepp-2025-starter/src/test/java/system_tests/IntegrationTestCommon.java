package system_tests;

import java.util.stream.Stream;

public class IntegrationTestCommon
{
    public static final String[] addTestCourse1 = {
            "3",
            "0",
            "TEST111",
            "Test Course",
            "Integration test course",
            "y",
            "Tester",
            "test@testsite",
            "testsec",
            "testsec@testsite",
            "12", //num tuts
            "4", //num labs
            "2", //add activity
            "TEST111",
            "1", //add lecture
            "2025-01-01",
            "06:00:00",
            "2025-01-01",
            "07:00:00",
            "Testvenue",
            "MONDAY",
            "LECTURE",
            "30",            //capacity
    };

    public static final String[] exit = {
            "-1", "-1" //exit
    };

    //flatten and concatenate an arbitrary number
    public static String[] concatUserInputs(String[]... all){
        String[] out = Stream.of(all).flatMap(Stream::of)
                .toArray(String[]::new);
        return out;
    }

}
