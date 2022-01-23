package com.testsigma.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidParameterException extends AuthenticationException {
  public InvalidParameterException(String msg, Throwable t) {
    super(msg, t);
  }

  public InvalidParameterException(String msg) {
    super(msg);
  }
}
