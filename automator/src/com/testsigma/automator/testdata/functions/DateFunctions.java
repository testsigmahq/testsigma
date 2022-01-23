package com.testsigma.automator.testdata.functions;

import com.github.javafaker.DateAndTime;
import com.github.javafaker.Faker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateFunctions {

  DateAndTime date = null;

  public DateFunctions() {
    date = new Faker().date();
  }

  public String current(String format) throws ParseException {

    return dateToString(new Date(), format);

  }

  public String future(int atMost, TimeUnit unit, String format) throws ParseException {

    return dateToString(date.future(atMost, unit), format);

  }

  public String between(String from, String to, String format) throws ParseException {

    return dateToString(date.between(parse(from, format), parse(to, format)), format);
  }

  public String future(int atMost, TimeUnit unit, String referenceDate, String format) throws ParseException {

    return dateToString(date.future(atMost, unit, parse(referenceDate, format)), format);
  }


  public String birthday(String format) throws ParseException {

    return dateToString(date.birthday(), format);
  }

  public String past(int atMost, TimeUnit unit, String format) throws ParseException {

    return dateToString(date.future(atMost, unit), format);

  }

  public String past(int atMost, TimeUnit unit, String referenceDate, String format) throws ParseException {

    return dateToString(date.future(atMost, unit, parse(referenceDate, format)), format);
  }

  public String dateBeforeToday(int noOfDays, String format) throws ParseException {
    Date d = new Date();
    Date dateBefore = new Date(d.getTime() - noOfDays * 24 * 3600 * 1000l);
    return dateToString(dateBefore, format);
  }

  public String dateAfterToday(int noOfDays, String format) throws ParseException {
    Date d = new Date();
    Date dateAfter = new Date(d.getTime() + noOfDays * 24 * 3600 * 1000l);
    return dateToString(dateAfter, format);
  }

  public String daysBeforeGivenDate(String date, int noOfDays, String format) throws ParseException {
    Date dateBefore = new Date(parse(date, format).getTime() - noOfDays * 24 * 3600 * 1000l);
    return dateToString(dateBefore, format);
  }

  public String daysAfterGivenDate(String date, int noOfDays, String format) throws ParseException {
    Date dateAfter = new Date(parse(date, format).getTime() + noOfDays * 24 * 3600 * 1000l);
    return dateToString(dateAfter, format);
  }


  private Date parse(String dateStr, String format) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    Date convertedDate = dateFormat.parse(dateStr);
    return convertedDate;
  }

  private String dateToString(Date date, String format) throws ParseException {
    SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    String dateString = dateFormat.format(date);
    return dateString;
  }
}
