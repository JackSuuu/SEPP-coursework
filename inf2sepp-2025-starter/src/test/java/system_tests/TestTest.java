package system_tests;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import model.KioskLogger;
import model.TimeSlot;
import org.junit.jupiter.api.Test;

public class TestTest extends TUITest {

    private void simpleFunc() {
        System.out.println("Enter input");
        Scanner in = new Scanner(System.in);
        System.out.println(in.nextLine());
        in.close();
    }

    @Test
    void doStuff() {
        setMockInput("david", "Jones");
        simpleFunc();
        simpleFunc();
    }

    @Test
    void doStuffTwo() {
        setMockInput("david");
        simpleFunc();
        setMockInput("jones");
        simpleFunc();
    }

    public List<String> testString;

    String[] setupTestCourse = {
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
            "12",
            "4",
            "2", //add activity
            "TEST111",
            "1", //add lecture
            "2025-01-01",
            "06:00:00",
            "2025-01-01",
            "07:00:00",
            "Testbuilding",
            "MONDAY",
            "TUTORIAL",     //discrepancy
            "30",            //capacity
            "4"            //view courses
    };

    String[] exitMenuString = {
            "-1",
            "-1"
    };

    public String[] concatStrings(String[]... all){
        String[] out = Stream.of(all).flatMap(Stream::of)
                .toArray(String[]::new);
        return out;
    }

    @Test
    public void stringtest(){
        String[] test = concatStrings(setupTestCourse, exitMenuString, exitMenuString);
        for(String s : test){
            System.out.println(s);
        }

    }

    @Test
    public void doLog(){
        KioskLogger.getInstance().log("testemail@company", "testUseCase", "0,0,1,help", "SUCCESS");
    }

}
