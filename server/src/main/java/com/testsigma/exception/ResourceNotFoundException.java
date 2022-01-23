/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.exception;

public class ResourceNotFoundException extends TestsigmaWebException {
  public ResourceNotFoundException(String errorMessage) {
    super("0", errorMessage);
  }
}
