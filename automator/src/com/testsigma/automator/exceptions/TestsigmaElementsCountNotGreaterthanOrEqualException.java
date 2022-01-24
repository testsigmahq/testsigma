package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class TestsigmaElementsCountNotGreaterthanOrEqualException extends AutomatorException {

  private Integer errorCode;
  private String message;
  private String dispMessage;

  public TestsigmaElementsCountNotGreaterthanOrEqualException(Integer errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public TestsigmaElementsCountNotGreaterthanOrEqualException(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    this.setRoot(this);
    this.setIsRoot(true);
    log.error(ex);
  }

  public TestsigmaElementsCountNotGreaterthanOrEqualException(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }

  public TestsigmaElementsCountNotGreaterthanOrEqualException(String exceptionMessage) {
    super(exceptionMessage);
    errorCode = 0;
    this.message = exceptionMessage;
    this.setRoot(this);
    this.setIsRoot(true);
    log.error(message);
  }

  public TestsigmaElementsCountNotGreaterthanOrEqualException(Integer errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }
}
