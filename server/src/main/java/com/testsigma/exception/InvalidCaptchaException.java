package com.testsigma.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidCaptchaException extends AuthenticationException {
  public InvalidCaptchaException(String msg, Throwable t) {
    super(msg, t);
  }

  public InvalidCaptchaException(String msg) {
    super(msg);
  }
}

