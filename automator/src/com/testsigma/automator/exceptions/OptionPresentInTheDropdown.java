package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class OptionPresentInTheDropdown extends AutomatorException {

  private Integer errorCode;
  private String message;
  private String dispMessage;

  public OptionPresentInTheDropdown(Integer errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public OptionPresentInTheDropdown(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    log.error(ex);
  }

  public OptionPresentInTheDropdown(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }

  public OptionPresentInTheDropdown(String exceptionMessage) {
    super(exceptionMessage);
    errorCode = 0;
    this.message = exceptionMessage;
    log.error(message);
  }

  public OptionPresentInTheDropdown(Integer errorCode, String message) {
    super(errorCode, message);
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }

}
