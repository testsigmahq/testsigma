package com.testsigma.util;

import lombok.extern.log4j.Log4j2;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
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
}
