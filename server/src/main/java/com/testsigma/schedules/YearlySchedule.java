package com.testsigma.schedules;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;

@Component
public class YearlySchedule extends Schedule {
  @Override
  public Timestamp getNextSchedule(Timestamp scheduleTime) {
    return super.changeNextInterval(scheduleTime, Calendar.YEAR, 1);
  }

}
