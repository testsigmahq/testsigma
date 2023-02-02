package com.testsigma.agent.services;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@Service
public class AgentService {
  public static String getComputerName() {
    String name = getHostName();
    if (StringUtils.isNotBlank(name)) {
      return name;
    }
    String hostName = getInetHostName();
    if (!hostName.isEmpty())
      return hostName;
    else
      return "";
  }

  private static String getHostName() {
    return SystemUtils.IS_OS_WINDOWS ? System.getenv("COMPUTERNAME") : System.getenv("HOSTNAME");
  }

  private static String getInetHostName() {
    String hostName = "";
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      log.error(e.getMessage(), e);
    }
    return hostName;
  }

  public static String getOsVersion() {
    String osVersion = SystemUtils.OS_VERSION;
    if (StringUtils.isNotBlank(osVersion)) {
      if (SystemUtils.IS_OS_WINDOWS) {
        if (osVersion.startsWith("6.1")) {
          osVersion = "7";
        } else if (osVersion.startsWith("6.2")) {
          osVersion = "8";
        } else if (osVersion.startsWith("6.3")) {
          osVersion = "8.1";
        } else if (osVersion.startsWith("10.0.2")){
          osVersion = "11";
        } else if (osVersion.startsWith("10.0")) {
          osVersion = "10";
        }
      } else if (SystemUtils.IS_OS_MAC_OSX) {
        if (osVersion.startsWith("10.16")) {
          osVersion = "11.0";
        }
      }
    }
    return osVersion;
  }
}
