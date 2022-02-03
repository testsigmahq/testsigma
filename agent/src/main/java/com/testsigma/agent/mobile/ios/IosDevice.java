package com.testsigma.agent.mobile.ios;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class IosDevice {
  UsbMuxSocket usbMuxSocketConnection;
  Device device;
}
