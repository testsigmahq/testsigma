package com.testsigma.automator.actions.exceptions;

import com.testsigma.automator.exceptions.AutomatorException;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class NaturalActionException extends AutomatorException {

  @Getter
  private final String message;
  @Getter
  private Integer errorCode;
  @Getter
  private String errorStackTraceTruncated;

  public NaturalActionException(String msg, Exception ex, int errorCode) {
    super(msg, ex);
    this.errorCode = errorCode;
    this.message = msg;
    this.errorStackTraceTruncated = truncate(ExceptionUtils.getStackTrace(ex));
  }

  public NaturalActionException(String message) {
    super(message);
    this.message = message;
  }

  private String truncate(String stackTrace) {
    if (stackTrace.length() > 30000) {
      return stackTrace.substring(0, 30000);
    }
    return stackTrace;
  }

}
