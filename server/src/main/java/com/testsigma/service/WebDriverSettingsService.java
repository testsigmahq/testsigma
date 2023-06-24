/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;


import com.testsigma.dto.WebDriverSettingsDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.factory.DriverSettingsServiceFactory;
import com.testsigma.model.*;
import com.testsigma.web.request.WebDriverSettingsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
@Log4j2
public class WebDriverSettingsService {
  private final MobileInspectionService mobileInspectionService;
  private final AgentDeviceService agentDeviceService;
  private final WebApplicationContext webApplicationContext;
  private final TestDeviceResultService testDeviceResultService;
  private final TestDeviceService testDeviceService;

  private final TestPlanResultService testPlanResultService;

  public WebDriverSettingsDTO getWebDriverSettings(WebDriverSettingsRequest webDriverSettingsRequest) throws SQLException,
    TestsigmaException, IOException {
    TestDevice testDevice = new TestDevice();
    TestPlanResult testPlanResult = new TestPlanResult();
    WorkspaceType workspaceType = webDriverSettingsRequest.getWorkspaceType();

    MobileInspection mobileInspection = this.mobileInspectionService.find(webDriverSettingsRequest.getMobileSessionId());
    if (mobileInspection.getLabType().isHybrid()) {
      if (mobileInspection.getAgentDeviceId() != null) {
        AgentDevice agentDevice = agentDeviceService.find(mobileInspection.getAgentDeviceId());
      }
    }

    testDevice.setAppPathType(mobileInspection.getApplicationPathType());
    if(mobileInspection.getUploadVersionId()!=null) {
      testDevice.setAppUploadId(mobileInspection.getUploadVersion().getUploadId());
      testDevice.setAppUploadVersionId(mobileInspection.getUploadVersionId());
    }
    else {
      testDevice.setAppActivity(mobileInspection.getAppActivity());
      testDevice.setAppPackage(mobileInspection.getApplicationPackage());
    }
    if(mobileInspection.getBundleId()!=null)
      testDevice.setAppBundleId(mobileInspection.getBundleId());
    testDevice.setUdid(String.valueOf(webDriverSettingsRequest.getUniqueId()));
    testDevice.setDeviceId(mobileInspection.getAgentDeviceId());

    WebDriverSettingsDTO webDriverSettingsDTO = getDriverCapabilities(testDevice, workspaceType,
      webDriverSettingsRequest.getExecutionLabType(), testPlanResult);

    List<Capability> capabilitiesSettings = mobileInspection.getCapabilities();
    List<WebDriverCapability> desiredCapabilities = new ArrayList<>();
    if (capabilitiesSettings != null) {
      capabilitiesSettings.forEach(data -> {
        desiredCapabilities.add(new WebDriverCapability(data.getName(), data.getValue()));
      });
    }
    webDriverSettingsDTO.getWebDriverCapabilities().addAll(desiredCapabilities);
    return webDriverSettingsDTO;
  }

  public WebDriverSettingsDTO getDriverCapabilities(TestDevice testDevice,
                                                    WorkspaceType workspaceType, TestPlanLabType testPlanLabType,TestPlanResult testPlanResult)
    throws SQLException, TestsigmaException, IOException {
    DriverSettingsServiceFactory driverSettingsServiceFactory = new DriverSettingsServiceFactory(webApplicationContext);
    DriverSettingsService driverSettingsService = driverSettingsServiceFactory.driverSettingsService(testPlanLabType);
    return driverSettingsService.driverSettings(testDevice, workspaceType, testPlanLabType, testPlanResult,
      driverSettingsService.getLabDetails(), webApplicationContext);
  }

  public WebDriverSettingsDTO getCapabilities(long id) throws TestsigmaException, IOException, SQLException {
    TestDeviceResult testDeviceResult = testDeviceResultService.find(id);
    TestDevice testDevice = testDeviceService.find(testDeviceResult.getTestDeviceId());
    WorkspaceType workspaceType = testDeviceResult.getTestDevice()
      .getWorkspaceVersion().getWorkspace().getWorkspaceType();
    TestPlanLabType testPlanLabType = testDeviceResult.getTestDevice()
      .getTestPlanLabType();
    TestPlanResult testPlanResult = testPlanResultService.find(testDeviceResult.getTestPlanResultId());
    return getDriverCapabilities(testDevice, workspaceType, testPlanLabType, testPlanResult);
  }
}
