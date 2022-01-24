package com.testsigma.agent.mobile.ios;

import lombok.extern.log4j.Log4j2;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;

@Log4j2
public class UsbMuxPosixSocket extends UsbMuxSocket {
  public UsbMuxPosixSocket(int tag) {
    super(tag);
    try {
      this.address = new AFUNIXSocketAddress(new File("/var/run/usbmuxd"));
      this.socket = AFUNIXSocket.newInstance();
      socket.connect(address);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
