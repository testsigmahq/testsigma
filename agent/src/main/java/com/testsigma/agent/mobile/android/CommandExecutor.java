/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile.android;

import com.testsigma.agent.exception.AdbCommandExecutionException;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Log4j2
public class CommandExecutor {
  public ArrayList<String> executeCommand(IDevice idevice, String command) throws AdbCommandExecutionException {
    ArrayList<String> list = new ArrayList<>();
    try {
      idevice.executeShellCommand(command, new ShellOutputReceiver(list));
    } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
      throw new AdbCommandExecutionException(e.getMessage(), e);
    }
    return list;
  }

  public Integer getScreenWidth(IDevice device) throws AdbCommandExecutionException {
    List<String> output = this.executeCommand(device, "dumpsys window displays");
    Matcher matcher;
    int screenWidth = 0;
    for (String str : output) {
      if ((matcher = Pattern.compile("init=(\\d+)x(\\d+) .*cur=*").matcher(str)).find()) {
        screenWidth = Integer.parseInt(matcher.group(1));
        break;
      }
    }
    return screenWidth;
  }

  public Integer getScreenHeight(IDevice device) throws AdbCommandExecutionException {
    List<String> output = this.executeCommand(device, "dumpsys window displays");
    Matcher matcher;
    int screenHeight = 0;
    for (String str : output) {
      if ((matcher = Pattern.compile("init=(\\d+)x(\\d+) .*cur=*").matcher(str)).find()) {
        screenHeight = Integer.parseInt(matcher.group(2));
        break;
      }
    }
    return screenHeight;
  }

  public boolean isPackageInstalled(IDevice device, String appPackage) throws AdbCommandExecutionException {
    log.debug("Checking if package is installed for package - " + appPackage);
    List<String> outputList = executeCommand(device, "pm list package " + appPackage);
    boolean isInstalled = false;
    isInstalled = (outputList.size() > 0);
    log.info(appPackage + " installation status on device " + device.getSerialNumber() + " is ::" + isInstalled);
    return isInstalled;
  }

  public String getChromeVersion(IDevice device) throws AdbCommandExecutionException {
    List<String> output = this.executeCommand(device, "dumpsys package com.android.chrome | grep versionName");
    String version = null;
    for (String str : output) {
      if (Pattern.compile("versionName=").matcher(str).find()) {
        version = str.replaceAll("versionName=", "");
        break;
      }
    }
    return version;
  }
}
