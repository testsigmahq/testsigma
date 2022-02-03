package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class TestsigmaSwitchValidationException extends AutomatorException {

  private Integer errorCode;
  private String message;
  private String dispMessage;

  public TestsigmaSwitchValidationException(Integer errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public TestsigmaSwitchValidationException(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    log.error(ex);
  }

  public TestsigmaSwitchValidationException(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }

  public TestsigmaSwitchValidationException(String exceptionMessage) {
    super(exceptionMessage);
    this.setRoot(this);
    this.setIsRoot(true);
    errorCode = 0;
    this.message = exceptionMessage;
    log.error(message);
  }

  public TestsigmaSwitchValidationException(Integer errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }
}
