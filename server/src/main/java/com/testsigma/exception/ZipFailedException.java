package com.testsigma.exception;

public class ZipFailedException extends TestsigmaWebException {
  public ZipFailedException(String errorCode) {
    super(errorCode);
  }
}
