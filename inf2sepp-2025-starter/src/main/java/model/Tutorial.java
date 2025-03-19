package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Tutorial extends Activity {
    private final int capacity;

    public Tutorial(int id, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String location, DayOfWeek day, int capacity) {
        super(id, startDate, startTime, endDate, endTime, location, day);
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return super.toString() + ", Capacity: " + capacity;
    }
}

