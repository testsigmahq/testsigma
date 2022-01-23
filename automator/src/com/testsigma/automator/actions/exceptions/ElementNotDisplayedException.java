package com.testsigma.automator.actions.exceptions;

public class ElementNotDisplayedException extends NaturalActionException {
  public ElementNotDisplayedException(String msg, Exception ex, int errorCode) {
    super(msg, ex, errorCode);
  }

  public ElementNotDisplayedException(String message) {
    super(message);
  }
}
