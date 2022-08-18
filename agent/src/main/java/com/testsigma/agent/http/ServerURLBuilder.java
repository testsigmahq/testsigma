/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.http;


import com.testsigma.automator.AutomatorConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
public class ServerURLBuilder {
  private final static String API_BASE_URI = "/api/agents";
  private final static String agentURI = API_BASE_URI + "/{uuid}";
  private final static String deviceURI = agentURI + "/devices/{deviceUUID}";
  private final static String deviceListURI = agentURI + "/devices";
  private final static String deviceStatusURI = deviceListURI + "/status";
  private final static String deviceDeveloperImageURI = deviceListURI + "/developer/{osVersion}/";
  private final static String wdaRealDeviceDownloadURI = deviceListURI + "/{deviceUuid}/wda_real_device";
  private final static String wdaEmulatorDownloadURI = deviceListURI + "/wda_emulator";
  private final static String xcTestDownloadURI = deviceListURI + "/xctest";
  private final static String executionURI = agentURI + "/execution";
  private final static String executableURI = agentURI + "/driver/executable_path";
  private final static String webServerConfigURI = agentURI + "/webserver/config";
  private final static String mobileSessionURI = agentURI + "/mobile_inspections/{id}";
  private final static String environmentResultURI = API_BASE_URI + "/test_device_results/{environmentRunResultId}";
  private final static String webdriverSettingsURI = API_BASE_URI + "/webdriver-settings";
  private final static String webServerCertificateFetchURI = API_BASE_URI + "/certificate";
  private final static String registerAgentURI = "/local/agents/register/{hostName}";
  private final static String testSuiteResultURI = API_BASE_URI + "/test_suite_results/{suiteResultId}";
  private final static String testCaseResultURI = API_BASE_URI + "/test_case_results/{testCaseResultId}";
  private final static String testCaseURI = API_BASE_URI + "/test_case/{testCaseId}";


  private final static String environmentResultUpdateURI = API_BASE_URI + "/test_device_results/{environmentResultId}/result";
  private final static String testSuiteResultUpdateURI = API_BASE_URI + "/test_suite_results/{testSuiteResultId}/result";
  private final static String testCaseResultUpdateURI = API_BASE_URI + "/test_case_results/{testCaseResultId}/result";

  private final static String elementURI = API_BASE_URI + "/elements/{elementName}";


  private final static String runTimeDataURI = API_BASE_URI + "/run_time_data/{parameterName}";
  private final static String runTimeDataParamURI = API_BASE_URI + "/run_time_data/variable";

  private final static String capabilitiesURI = API_BASE_URI + "/webdriver-settings/capabilities/{environmentRunId}";
  private final static String suggestionsURI = API_BASE_URI + "/suggestions/{naturalTextActionId}";

  public static String capabilitiesURL(Long environmentRunId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(capabilitiesURI).build().expand(environmentRunId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String elementURL(String elementName) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(elementURI).build().expand(elementName).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String testCaseResultURL(Long testCaseResultId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(testCaseResultURI).build().expand(testCaseResultId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String testSuiteResultURL(Long suiteResultId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(testSuiteResultURI).build().expand(suiteResultId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String runTimeDataURL(String parameterName,
                                      MultiValueMap<String, String> queryParams) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(runTimeDataURI).queryParams(queryParams).build()
        .expand(parameterName).encode();
    return serverURL + StringUtils.replace(uriComponents.toUriString(), "+", "%2B");
  }

  public static String runTimeNewDataURL(MultiValueMap<String, String> queryParams) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(runTimeDataParamURI).queryParams(queryParams).build().encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String testCaseDetailsURL(Long testCaseId, MultiValueMap<String, String> queryParams) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(testCaseURI).queryParams(queryParams).build().expand(testCaseId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String environmentResultUpdateURL(Long environmentResultId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(environmentResultUpdateURI).build().expand(environmentResultId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String testSuiteResultUpdateURL(Long testSuiteResultId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(testSuiteResultUpdateURI).build().expand(testSuiteResultId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String testCaseResultUpdateURL(Long testCaseResultId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(testCaseResultUpdateURI).build().expand(testCaseResultId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String agentURL(String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(agentURI).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String environmentResultURL(Long environmentRunResultId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(environmentResultURI).build().expand(environmentRunResultId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String agentConnectedDeviceURL(String uuid, String deviceUUID) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(deviceURI).build().expand(uuid, deviceUUID).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String agentConnectedDevicesURL(String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(deviceListURI).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String agentDeviceStatusURL(String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(deviceStatusURI).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String executionURL(String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(executionURI).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String registerAgentURL(MultiValueMap<String, String> queryParams) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    String fragmentWithQueryParams =
      UriComponentsBuilder.fromPath("/agents/new").queryParams(queryParams)
        .toUriString();

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(serverURL + "/")
        .fragment(fragmentWithQueryParams)
        .build().expand();
    return uriComponents.toUriString();
  }

  public static String webDriverSettingsURL() {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(webdriverSettingsURI).build().encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String driverExecutableURL(MultiValueMap<String, String> queryParams, String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(executableURI).queryParams(queryParams).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String webServerCertificateFetchURL() {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(webServerCertificateFetchURI).build().encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String registerLocalAgentURL(String hostName, String serverURL) {
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(registerAgentURI).build().expand(hostName).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String mobileSessionURL(String uuid, Long mobileSessionId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(mobileSessionURI).build().expand(uuid, mobileSessionId).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String deviceDeveloperImageURL(String uuid, String osVersion) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(deviceDeveloperImageURI).build().expand(uuid, osVersion).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String wdaRealDeviceDownloadURL(String uuid, String deviceUuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(wdaRealDeviceDownloadURI).build().expand(uuid, deviceUuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String wdaEmulatorDownloadURL(String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
            UriComponentsBuilder.fromUriString(wdaEmulatorDownloadURI).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String XcTestDownloadURL(String uuid) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
            UriComponentsBuilder.fromUriString(xcTestDownloadURI).build().expand(uuid).encode();
    return serverURL + uriComponents.toUriString();
  }

  public static String suggestionsURL(Integer naturalTextActionId) {
    String serverURL = AutomatorConfig.getInstance().getCloudServerUrl();
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(suggestionsURI).build().expand(naturalTextActionId).encode();
    return serverURL + uriComponents.toUriString();
  }
}
