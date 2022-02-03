package com.testsigma.exception;

public class TestsigmaDatabaseException extends TestsigmaException {
  /**
   *
   */
  private int type = 0;

  public TestsigmaDatabaseException(String errorCode) {
    super(errorCode);
  }

  public TestsigmaDatabaseException(String errorCode, Exception ex) {
    super(errorCode, ex);
  }

  public TestsigmaDatabaseException(String errorCode, Exception ex, int type) {
    super(errorCode, ex);
    this.type = type;
  }

  public TestsigmaDatabaseException(Exception ex) {
    super(ex);
  }

  public TestsigmaDatabaseException(Exception ex, int type) {
    super(ex);
    this.type = type;
  }

  public TestsigmaDatabaseException(String errorCode, String message) {
    super(errorCode, message);
  }

  public TestsigmaDatabaseException(String errorCode, String message, String details) {
    super(errorCode, message, details);
  }

  public int getType() {
    return type;
  }
}

