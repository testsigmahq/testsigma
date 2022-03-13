package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.AppPathType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDeviceSettingsDTO {

  public String title;
  private String osVersion;
  private String browser;
  private String browserVersion;
  private String resolution;
  private String platform;
  private String type;
  private String deviceName;
  private String appiumUrl;
  @JsonProperty(value = "app_upload_id")
  private String appUploadId;
  private String appPackage;
  private String appActivity;
  private String capabilities;
  private AppPathType appPathType;
  private String appUrl;
  private String appId;
  private String executionName;
  private Long environmentParamId;
  @JsonProperty(value = "user_name")
  private String userName;
  private String password;
  private String runBy;
  private String tenant;
  private Long envRunId;
  private String jwtApiKey;
  private Integer elementTimeout;
  private Integer pageLoadTimeout;
  private Long executionRunId;

  private String hybridBrowserDriverPath;
  private String chromedriverExecutableDir;

  @JsonProperty(value = "native_test_object_key")
  private String nativeTestObjectKey;
  private String testObjectApiKey;
  @JsonProperty(value = "create_session_at_case_level")
  private Boolean createSessionAtCaseLevel;
  @JsonProperty("apksrcType")
  private String androidApkSourceType;
  @JsonProperty("ipasrcType")
  private String iosSourceType;

}
