/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
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
  }

  public TestsigmaException(String errorCode, Exception ex) {
    super(errorCode, ex);
    this.errorCode = errorCode;
    this.message = errorCode;
    this.displayMessage = errorCode;
  }

  public TestsigmaException(Exception ex) {
    super(ex);
    this.message = ex.getMessage();
    this.displayMessage = ex.getLocalizedMessage();
  }

  public TestsigmaException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.message = message;
    this.displayMessage = message;
  }

  public TestsigmaException(String errorCode, String message, String details) {
    this.errorCode = errorCode;
    this.message = message;
    this.displayMessage = message;
    this.details = details;
  }
}
