package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class TestsigmaVerificationFailure extends AutomatorException {

  private Integer errorCode;
  private String message;
  private String details;
  private String dispMessage;

  public TestsigmaVerificationFailure(Integer errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public TestsigmaVerificationFailure(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    log.error(ex);
  }

  public TestsigmaVerificationFailure(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }

  public TestsigmaVerificationFailure(String exceptionMessage) {
    super(exceptionMessage);
    errorCode = 0;
    this.message = exceptionMessage;
    this.setRoot(this);
    this.setIsRoot(true);
    log.error(message);
  }

  public TestsigmaVerificationFailure(Integer errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }


  public TestsigmaVerificationFailure(String message, String details) {
    super(message, details);
    this.message = message;
    this.details = details;
    log.error(message);
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}
