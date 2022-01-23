package com.testsigma.exception;

public class NotLocalAgentRegistrationException extends TestsigmaException {
  public NotLocalAgentRegistrationException(String errorCode) {
    super(errorCode);
  }

  public NotLocalAgentRegistrationException(String errorCode, String message) {
    super(errorCode, message);
  }
}
