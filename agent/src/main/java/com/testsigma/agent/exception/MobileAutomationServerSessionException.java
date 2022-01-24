/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

public class MobileAutomationServerSessionException extends Exception {
  public MobileAutomationServerSessionException(String description) {
    super(description);
  }

  public MobileAutomationServerSessionException(String description, Throwable e) {
    super(description, e);
  }
}
