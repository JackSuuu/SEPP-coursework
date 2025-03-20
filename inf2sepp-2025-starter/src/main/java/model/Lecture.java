package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Lecture extends Activity {
    private final boolean recordingEnabled;

    public Lecture(int id, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String location, DayOfWeek day, boolean recordingEnabled) {
        super(id, startDate, startTime, endDate, endTime, location, day);
        this.recordingEnabled = recordingEnabled;
    }

    @Override
    public String toString() {
        return super.toString() + ", Recording Enabled: " + recordingEnabled;
    }
}