package com.testsigma.schedules;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class OnlyOnceSchedule extends Schedule {


  @Override
  public Timestamp getNextSchedule(Timestamp scheduleTime) {
    return null;
  }
}
