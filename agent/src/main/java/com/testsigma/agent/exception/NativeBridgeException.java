/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

public class NativeBridgeException extends Exception {
  public NativeBridgeException(String description) {
    super(description);
  }

  public NativeBridgeException(String description, Throwable e) {
    super(description, e);
  }
}
