/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenMissingException extends AuthenticationException {
  public JwtTokenMissingException(String msg) {
    super(msg);
  }
}
