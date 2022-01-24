package com.testsigma.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
  public static final SimpleDateFormat shortDateFormat =
    new SimpleDateFormat(System.getProperty("short.date.format", "yyyy-MM-dd HH:mm:ss"));

  public static String formatDateShortNew(Date d) {
    return d == null ? "null" : shortDateFormat.format(d);
  }

}
