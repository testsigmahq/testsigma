package com.testsigma.automator.formatters;

import com.testsigma.automator.exceptions.AutomatorException;

public class NumberFormatter {

  public static Double getDoubleValue(String doubleText) throws AutomatorException {
    return getDoubleValue(doubleText, null);
  }

  public static Integer getIntegerValue(String integerText) throws AutomatorException {
    return getIntegerValue(integerText, null);
  }

  public static Double getDoubleValue(String text, String errorMessage) throws AutomatorException {
    try {
      return Double.parseDouble(text.trim());
    } catch (NumberFormatException e) {
      errorMessage = (errorMessage != null) ? errorMessage : "Invalid number: " + text;
      throw new AutomatorException(errorMessage);
    }
  }

  public static Integer getIntegerValue(String text, String errorMessage) throws AutomatorException {
    try {
      return Integer.parseInt(text.trim());
    } catch (NumberFormatException e) {
      errorMessage = (errorMessage != null) ? errorMessage : "Invalid number: " + text;
      throw new AutomatorException(errorMessage);
    }
  }

}
