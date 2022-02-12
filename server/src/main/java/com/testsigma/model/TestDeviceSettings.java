package com.testsigma.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDeviceSettings {
  public String title;
  private String osVersion;
  private String browser;
  private String browserVersion;
  private String resolution;
  private Platform platform;
  private String type;
  private String deviceName;
  private String appiumUrl;
  @JsonProperty("app_upload_id")
  private String appUploadId;
  private String appPackage;
  private String appActivity;
  private String appId;
  private String capabilities;
  private AppPathType appPathType;
  private String appUrl;
  private String executionName;
  private Long environmentParamId;
  @JsonProperty(value = "user_name")
  private String userName;
  private String password;
  private String runBy;
  private String tenant;
  private Long envRunId;
  private Long deviceId;
  private Boolean isLatest;
  private Integer elementTimeout;
  private Integer pageLoadTimeout;
  private Long executionRunId;
  private String deviceNameCapability;
  private String extendedDebugging;
  private String hybridBrowserDriverPath;
  private String skipServerInstallation;
  private String skipDeviceInitialization;
  private String chromedriverExecutableDir;
  private String nativeWebScreenshot;
  private String udid;
  @JsonProperty(value = "native_test_object_key")
  private String nativeTestObjectKey;
  private String testObjectApiKey;
  @JsonProperty(value = "create_session_at_case_level")
  private Boolean createSessionAtCaseLevel;
}
