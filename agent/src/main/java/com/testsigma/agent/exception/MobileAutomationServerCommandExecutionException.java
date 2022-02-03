/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

public class MobileAutomationServerCommandExecutionException extends Exception {
  public MobileAutomationServerCommandExecutionException(String description) {
    super(description);
  }

  public MobileAutomationServerCommandExecutionException(String description, Throwable e) {
    super(description, e);
  }
}
