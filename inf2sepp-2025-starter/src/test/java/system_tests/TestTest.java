package system_tests;

import org.junit.jupiter.api.BeforeEach;

import java.util.Scanner;
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
}
