package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class TestsigmaNoAlertPresentException extends AutomatorException {

  private Integer errorCode;
  private String message;
  private String dispMessage;

  public TestsigmaNoAlertPresentException(Integer errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public TestsigmaNoAlertPresentException(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    log.error(ex);
  }

  public TestsigmaNoAlertPresentException(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }

  public TestsigmaNoAlertPresentException(String exceptionMessage) {
    super(exceptionMessage);
    errorCode = 0;
    this.message = exceptionMessage;
    log.error(message);
    this.setRoot(this);
    this.setIsRoot(true);
  }

  public TestsigmaNoAlertPresentException(Integer errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }
}
