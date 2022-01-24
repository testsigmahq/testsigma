/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.mobile;

import com.testsigma.agent.exception.MobileAutomationServerCommandExecutionException;
import com.testsigma.agent.mobile.android.CommandExecutor;
import com.testsigma.agent.request.TapPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class DeviceCommand {
  private final DeviceContainer deviceContainer;
  private final CommandExecutor commandExecutor;

  public void tap(String uniqueId, TapPoint tapPoint)
    throws MobileAutomationServerCommandExecutionException {
    try {
      MobileDevice mobileDevice = deviceContainer.getDevice(uniqueId);
      commandExecutor.executeCommand(mobileDevice.iDevice, "input tap " + tapPoint.getX() + " " + tapPoint.getY());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void swipe(String uniqueId, TapPoint[] tapPoints)
    throws MobileAutomationServerCommandExecutionException {
    try {
      MobileDevice mobileDevice = deviceContainer.getDevice(uniqueId);
      commandExecutor.executeCommand(mobileDevice.iDevice,
        "input swipe " + tapPoints[0].getX() + " " + tapPoints[0].getY() + " " + tapPoints[1].getX() + " " + tapPoints[1]
          .getY());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }

  public void back(String uniqueId) throws MobileAutomationServerCommandExecutionException {
    try {
      MobileDevice mobileDevice = deviceContainer.getDevice(uniqueId);
      commandExecutor.executeCommand(mobileDevice.iDevice, "input keyevent KEYCODE_BACK");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileAutomationServerCommandExecutionException(e.getMessage(), e);
    }
  }
}
