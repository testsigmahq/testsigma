/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

public class AdbCommandExecutionException extends Exception {

  public AdbCommandExecutionException(String description, Throwable e) {
    super(description, e);
  }
}
