/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.exception;

public class IntegrationNotFoundException extends TestsigmaWebException {

  /**
   * @param errorCode
   */
  public IntegrationNotFoundException(String errorCode) {
    super(errorCode);
  }

}
