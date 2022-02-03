package com.testsigma.agent.mobile.ios;

import lombok.Getter;

public class UsbMuxReplyException extends Exception {
  @Getter
  private final int errorCode;

  public UsbMuxReplyException(Integer errorCode) {
    super("USB Mux Response Error - " + UsbMuxErrorCode.getErrorCode(errorCode));
    this.errorCode = errorCode;
  }

  public UsbMuxReplyException(Integer errorCode, Throwable cause) {
    super("USB Mux Response Error - " + UsbMuxErrorCode.getErrorCode(errorCode), cause);
    this.errorCode = errorCode;
  }
}
