/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.exception;

public class DeviceContainerException extends Exception {

  public DeviceContainerException(String description, Throwable e) {
    super(description, e);
  }
}
