package com.testsigma.agent.mobile.ios;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UsbMuxErrorCode {
  OK,
  BAD_COMMAND,
  BAD_DEVICE,
  CONNECTION_REFUSED,
  BAD_VERSION;

  public static UsbMuxErrorCode getErrorCode(Integer id) {
    switch (id) {
      case 0:
        return OK;
      case 1:
        return BAD_COMMAND;
      case 2:
        return BAD_DEVICE;
      case 3:
        return CONNECTION_REFUSED;
      case 6:
        return BAD_VERSION;
    }
    return null;
  }
}
