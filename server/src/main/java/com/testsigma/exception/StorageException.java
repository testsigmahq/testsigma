package com.testsigma.exception;

public class StorageException extends Exception {
  public StorageException(String errorMessage, Exception exception) {
    super(errorMessage, exception);
  }
}
