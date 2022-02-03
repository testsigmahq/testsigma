package com.testsigma.agent.mobile.ios;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Log4j2
public class UsbMuxWindowsSocket extends UsbMuxSocket {
  public UsbMuxWindowsSocket(int tag) {
    super(tag);
    this.address = new InetSocketAddress("127.0.0.1", 27015);
    this.socket = new Socket();
    try {
      socket.connect(address);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
