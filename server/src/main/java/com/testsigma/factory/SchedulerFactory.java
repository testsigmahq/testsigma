package com.testsigma.factory;

import com.testsigma.model.ScheduleType;
import com.testsigma.schedules.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SchedulerFactory {

  private final OnlyOnceSchedule onlyOnceSchedule;
  private final HourlySchedule hourlySchedule;
  private final DailySchedule dailySchedule;
  private final WeeklySchedule weeklySchedule;
  private final BiWeeklySchedule biWeeklySchedule;
  private final MonthlySchedule monthlySchedule;
  private final YearlySchedule yearlySchedule;

  public Schedule getSchedule(ScheduleType scheduleType) {
    switch (scheduleType) {
      case HOURLY:
        return hourlySchedule;
      case DAILY:
        return dailySchedule;
      case WEEKLY:
        return weeklySchedule;
      case BIWEEKLY:
        return biWeeklySchedule;
      case MONTHLY:
        return monthlySchedule;
      case YEARLY:
        return yearlySchedule;
    }
    return onlyOnceSchedule;
  }

}
