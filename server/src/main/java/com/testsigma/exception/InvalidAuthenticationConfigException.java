/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.exception;

public class InvalidAuthenticationConfigException extends TestsigmaWebException {

  /**
   * @param errorCode
   */
  public InvalidAuthenticationConfigException(String errorCode) {
    super(errorCode);
  }

}
