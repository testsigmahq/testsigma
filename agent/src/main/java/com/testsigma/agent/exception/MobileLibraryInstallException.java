/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.exception;

public class MobileLibraryInstallException extends Exception {
  public MobileLibraryInstallException(String description) {
    super(description);
  }

  public MobileLibraryInstallException(String description, Throwable e) {
    super(description, e);
  }
}
