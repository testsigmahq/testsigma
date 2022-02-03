package com.testsigma.exception;

import org.springframework.security.core.AuthenticationException;

public class AgentDeletedException extends AuthenticationException {
  public AgentDeletedException(String msg) {
    super(msg);
  }
}
