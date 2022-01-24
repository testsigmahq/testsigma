package com.testsigma.exception;

public class TestsigmaWebException extends TestsigmaException {
  public TestsigmaWebException(String errorCode) {
    super(errorCode);
  }

  public TestsigmaWebException(String errorCode, String message) {
    super(errorCode, message);
  }
}

