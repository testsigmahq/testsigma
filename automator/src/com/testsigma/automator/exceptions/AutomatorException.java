package com.testsigma.automator.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Getter
@Setter
public class AutomatorException extends Exception {
  public static final Integer MESSAGE_MAX_SIZE = 500;
  public static String msgParamIdentifier = "\\?";

  private Integer errorCode;
  private String message;
  private String details;
  private String dispMessage;
  private String rootMsg;
  private boolean isRoot;
  private Throwable root;

  public AutomatorException(Integer errorCode) {
    this.errorCode = errorCode;
    log.error(errorCode);
  }

  public AutomatorException(String message, String details) {
    this.message = message;
    this.details = details;
    log.error(message);
  }

  public AutomatorException(Exception ex) {
    super(ex);
    this.dispMessage = ex.getLocalizedMessage();
    this.message = ex.getMessage();
    this.root = ex;
    log.error(ex);
  }

  public AutomatorException(String msg, Exception ex) {
    super(msg, ex);
    this.dispMessage = msg;
    this.message = msg;
    log.error(msg, ex);
  }


  public AutomatorException(String exceptionMessage) {
    errorCode = 0;
    this.message = exceptionMessage;
    log.error(message);
    this.setRoot(this);
    this.setIsRoot(true);
  }

  public AutomatorException(Integer errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    log.error(message);
  }

  public AutomatorException(Integer errorCode, String message, String details) {
    this.errorCode = errorCode;
    this.message = message;
    this.dispMessage = message;
    this.details = details;
    log.error(message);
    log.error("Details :: " + details);
  }

  public static String getMessage(String replaceableMsg, Object... replaceParameters) {
    replaceableMsg = replaceableMsg + " ";
    if ((replaceParameters == null) || (replaceParameters.length < 1))
      return replaceableMsg;
    int paramSize = replaceParameters.length;
    for (int i = 0; i < paramSize; i++) {
      String[] replacebleMsg = replaceableMsg.split(msgParamIdentifier + (i + 1));
      String tempStr = replacebleMsg[0];
      int replaceLength = replacebleMsg.length;
      for (int j = 1; j < replaceLength; j++) {
        String repMsg = replacebleMsg[j];
        String parm = (replaceParameters[i] != null) ? replaceParameters[i].toString() : "";
        tempStr = tempStr + parm + repMsg;
      }
      replaceableMsg = tempStr;
    }
    return replaceableMsg;
  }

  public static Object[] limitSize(Object[] arr, int limit) {
    if (arr == null) {
      return arr;
    }

    for (int i = 0; i < arr.length; i++) {
      if (arr[i] != null && arr[i] instanceof String && arr[i].toString().length() > limit) {
        arr[i] = arr[i].toString().substring(0, limit);
      }
    }
    return arr;
  }

  public boolean getIsRoot(boolean isRoot) {
    return this.isRoot;
  }

  public void setIsRoot(boolean isRoot) {
    this.isRoot = isRoot;
  }
}

