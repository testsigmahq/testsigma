/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.exception;

public class DeviceNotConnectedException extends Exception {
  public DeviceNotConnectedException(String description) {
    super(description);
  }

}
