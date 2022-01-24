/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile;

import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.constants.MobileOs;
import com.android.ddmlib.IDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Data
@NoArgsConstructor
public class MobileDevice {
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  RemoteWebDriver remoteWebDriver;
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  IDevice iDevice;
  private String name;
  private String uniqueId;
  private String muxDeviceId;
  private MobileOs osName;
  private String osVersion;
  private Boolean isEmulator;
  private Boolean isOnline;
  private Integer screenWidth;
  private Integer screenHeight;
  private String productModel;
  private String apiLevel;
  private String abi;
  private List<AgentBrowser> browserList;
  private ExecutorService wdaExecutorService;
  private ExecutorService wdaRelayExecutorService;
  private Process wdaProcess;
  private Process wdaRelayProcess;
}
