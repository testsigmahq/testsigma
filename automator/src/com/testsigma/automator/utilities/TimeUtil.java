package com.testsigma.automator.utilities;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormat;

import java.time.Instant;

public class TimeUtil {
  public static String getFormattedDuration(Instant start, Instant end) {
    Duration duration = new Duration(start.toEpochMilli(), end.toEpochMilli());
    Period period = duration.toPeriod().normalizedStandard(PeriodType.time());
    return PeriodFormat.getDefault().print(period);
  }
}
