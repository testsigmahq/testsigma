package com.testsigma.schedules;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
public abstract class Schedule {

  public abstract Timestamp getNextSchedule(Timestamp scheduleTime);

  protected Timestamp changeNextInterval(Timestamp scheduleTime,
                                         int weekOfMonth, int amount) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(sdf.parse(String.valueOf(scheduleTime)));
    } catch (ParseException e) {
      e.printStackTrace();
    }
    cal.add(weekOfMonth, amount);
    return Timestamp.valueOf(sdf.format(cal.getTime()));
  }

}
