package model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class TimeSlot {
    private final DayOfWeek day;
    private final LocalTime startTime;
    private final LocalTime endTime;
    public String courseCode = null;
    public int activityId = -1;
    public String status;

    public TimeSlot(DayOfWeek day, LocalTime startTime, LocalTime endTime, String courseCode, int activityId, String status) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courseCode = courseCode;
        this.activityId = activityId;
        this.status = status;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getCourseCode() {
        return this.courseCode;
    }

    public int getActivityId() {
        return this.activityId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean hasCourseCode() {
        return this.courseCode.equals(null);
    }

    public boolean hasActivityId() {
        return this.activityId == -1;
    }

    public boolean isChosen() {
        return this.status.equals("CHOSEN");
    }

    @Override
    public String toString() {
        return "\nTimeSlot {" +
               "\nday = " + day +
               "\n, startTime = " + startTime +
               "\n, endTime = " + endTime +
               "\n, courseCode = '" + courseCode + '\'' +
               "\n, activityId = " + activityId +
               "\n, status = '" + status + '\'' +
               "}\n";
    }
    
}
