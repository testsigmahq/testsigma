package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class TestsigmaTryCountExceedException extends AutomatorException {

  private Integer errorCode;
  private String message;
  private String details;
  private String dispMessage;

  public TestsigmaTryCountExceedException(Integer errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public TestsigmaTryCountExceedException(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    log.error(ex);
  }

  public TestsigmaTryCountExceedException(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }

  public TestsigmaTryCountExceedException(String exceptionMessage) {
    super(exceptionMessage);
    this.setRoot(this);
    errorCode = 0;
    this.message = exceptionMessage;
    log.error(message);
  }

  public TestsigmaTryCountExceedException(Integer errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }

  public TestsigmaTryCountExceedException(Integer errorCode, String message, String details) {
    super(errorCode, message, details);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    this.details = details;
    log.error(message);
    log.error("Details :: " + details);
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}
