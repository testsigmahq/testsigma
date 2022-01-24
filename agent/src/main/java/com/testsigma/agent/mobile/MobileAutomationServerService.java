/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.mobile;

import com.testsigma.agent.constants.MobileOs;
import com.testsigma.agent.exception.MobileLibraryInstallException;
import com.testsigma.agent.mobile.android.AdbBridge;
import com.testsigma.agent.mobile.android.AndroidMobileAutomationServer;
import com.testsigma.agent.mobile.ios.IosMobileAutomationServer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class MobileAutomationServerService {
  private final AndroidMobileAutomationServer androidMobileAutomationServer;
  private final IosMobileAutomationServer iosMobileAutomationServer;
  private final AdbBridge adbBridge;
  private final MobileAutomationServer mobileAutomationServer;

  public MobileAutomationServer getMobileAutomationServer() {
    return this.mobileAutomationServer;
  }

  public void installDrivers(MobileOs osName, String uniqueId)
    throws MobileLibraryInstallException {
    if (osName.equals(MobileOs.ANDROID)) {
      androidMobileAutomationServer.installDrivers(uniqueId);
    } else {
      iosMobileAutomationServer.installDrivers(uniqueId);
    }
  }

}
