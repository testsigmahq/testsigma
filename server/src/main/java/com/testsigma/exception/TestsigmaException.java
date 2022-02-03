package com.testsigma.exception;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class TestsigmaException extends Exception {
  private String errorCode;
  private String message;
  private String details;
  private String displayMessage;

  public TestsigmaException() {
  }

  public TestsigmaException(String errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    this.message = errorCode;
    this.displayMessage = errorCode;
    log.error(errorCode);
  }

  public TestsigmaException(String errorCode, Exception ex) {
    super(errorCode, ex);
    this.errorCode = errorCode;
    this.message = errorCode;
    this.displayMessage = errorCode;
    log.error(ex.getMessage(), ex);
  }

  public TestsigmaException(Exception ex) {
    super(ex);
    this.message = ex.getMessage();
    this.displayMessage = ex.getLocalizedMessage();
    log.error(ex.getMessage(), ex);
  }

  public TestsigmaException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.message = message;
    this.displayMessage = message;
    log.error("Error Code : " + errorCode);
    log.error("Message : " + message);
  }

  public TestsigmaException(String errorCode, String message, String details) {
    this.errorCode = errorCode;
    this.message = message;
    this.displayMessage = message;
    this.details = details;
    log.error("Error Code : " + errorCode);
    log.error("Message : " + message);
    log.error("Details : " + details);
  }

  public String getLocalizedMessage() {
    return this.message;
  }
}
