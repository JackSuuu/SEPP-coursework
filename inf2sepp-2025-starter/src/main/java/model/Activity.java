package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Activity {

    private int id;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private String location;
    private DayOfWeek day;

    public Activity(int id, LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime, String location, DayOfWeek day) {
        this.id = id;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.location = location;
        this.day = day;
    }

    public boolean hasId(int id) {
        return this.id == id;
    }

    public int getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public DayOfWeek getDay() {
        return day;
    }
  

    @Override
    public String toString() {
        return "Activity Attributes -> {" +
               ",\nid=" + id +
               ",\n startDate=" + startDate +
               ",\n startTime=" + startTime +
               ",\n endDate=" + endDate +
               ",\n endTime=" + endTime +
               ",\n location='" + location + '\'' +
               ",\n day=" + day +
               "}\n";
    }
}
