package com.testsigma.exception;

public class TestsigmaValidationException extends TestsigmaException {

  public TestsigmaValidationException(String errorCode) {
    super(errorCode);
  }

  public TestsigmaValidationException(String errorCode, String message) {
    super(errorCode, message);
  }
}

