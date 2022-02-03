package com.testsigma.exception;

import org.springframework.security.core.AuthenticationException;

public class JWTTokenException extends AuthenticationException {
  public JWTTokenException(String message, Throwable e) {
    super(message, e);
  }

  public JWTTokenException(String message) {
    super(message);
  }
}
