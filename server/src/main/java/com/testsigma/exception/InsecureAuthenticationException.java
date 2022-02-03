/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.exception;

public class InsecureAuthenticationException extends TestsigmaWebException {

  /**
   * @param errorCode
   */
  public InsecureAuthenticationException(String errorCode) {
    super(errorCode);
  }

  public InsecureAuthenticationException(String errorCode, String message) {
    super(errorCode, message);
  }
}
