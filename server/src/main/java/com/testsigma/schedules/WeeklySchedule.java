package com.testsigma.schedules;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;

@Component
public class WeeklySchedule extends Schedule {
  @Override
  public Timestamp getNextSchedule(Timestamp scheduleTime) {
    return super.changeNextInterval(scheduleTime, Calendar.WEEK_OF_MONTH, 1);
  }
}
