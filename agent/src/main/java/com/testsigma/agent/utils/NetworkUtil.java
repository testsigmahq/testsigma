/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

@Log4j2
public class NetworkUtil {

  public static String getCurrentIpAddress() {
    String ipAddr = null;
    try {
      InetAddress inetAddress = InetAddress.getLocalHost();
      ipAddr = inetAddress.getHostAddress();
      Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
        .getNetworkInterfaces();
      while (networkInterfaces.hasMoreElements()) {
        NetworkInterface networkInterface = networkInterfaces.nextElement();
        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          if (addr instanceof Inet4Address
            && !addr.isLoopbackAddress()) {
            return addr.getHostAddress();
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return ipAddr;
  }

  public static int getFreePort() throws IOException {
    ServerSocket socket;
    boolean portFound = false;
    int port = 10000;

    while (port <= 65535) {
      try {
        port = RandomUtils.nextInt(10000, 65535);
        socket = new ServerSocket(port);
        socket.close();
        portFound = true;
        break;
      } catch (Exception e) {
        log.debug("Unable to reserve port " + port + " ( " + e.getMessage() + " )");
      }
    }

    if (portFound) {
      return port;
    } else {
      throw new IOException("Unable to reserve any free port...");
    }
  }
}
