package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class BrowserInstanceNotFoundException extends AutomatorException {

  private String message;

  public BrowserInstanceNotFoundException(Integer errorCode) {
    super(errorCode);
    log.error(errorCode);
  }

  public BrowserInstanceNotFoundException(Exception ex) {
    super(ex);
    ex.getLocalizedMessage();
    this.message = ex.getMessage();
    log.error(ex);
  }

  public BrowserInstanceNotFoundException(String msg, Exception ex) {
    super(msg, ex);
    this.message = msg;
    log.error(msg, ex);
  }

  public BrowserInstanceNotFoundException(String exceptionMessage) {
    super(exceptionMessage);
    this.message = exceptionMessage;
    this.setRoot(this);
    this.setIsRoot(true);

    log.error(message);
  }

  public BrowserInstanceNotFoundException(Integer errorCode, String message) {
    super(errorCode, message);
    this.message = message;
    log.error(message);
  }

  public void setErrorCode(Integer errorCode) {
  }

}

