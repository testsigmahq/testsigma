package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.AppPathType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class APITestDeviceSettingsDTO {

  public String title;
  @JsonProperty("os_version")
  private String osVersion;
  @JsonProperty("browser")
  private String browser;
  @JsonProperty("browser_version")
  private String browserVersion;
  @JsonProperty("resolution")
  private String resolution;
  @JsonProperty("platform")
  private String platform;
  @JsonProperty("type")
  private String type;
  @JsonProperty("device_name")
  private String deviceName;
  @JsonProperty("appium_url")
  private String appiumUrl;
  @JsonProperty(value = "app_upload_id")
  private String appUploadId;
  @JsonProperty("app_package")
  private String appPackage;
  @JsonProperty("app_activity")
  private String appActivity;
  @JsonProperty("capabilities")
  private String capabilities;
  @JsonProperty("app_path_type")
  private AppPathType appPathType;
  @JsonProperty("app_url")
  private String appUrl;
  @JsonProperty("app_id")
  private String appId;
  @JsonProperty("execution_name")
  private String executionName;
  @JsonProperty("environment_param_id")
  private Long environmentParamId;
  @JsonProperty(value = "user_name")
  private String userName;
  @JsonProperty("password")
  private String password;
  @JsonProperty("run_by")
  private String runBy;
  @JsonProperty("tenant")
  private String tenant;
  @JsonProperty("env_run_id")
  private Long envRunId;
  @JsonProperty("jwt_api_key")
  private String jwtApiKey;
  @JsonProperty("element_timeout")
  private Integer elementTimeout;
  @JsonProperty("page_load_timeout")
  private Integer pageLoadTimeout;
  @JsonProperty("execution_run_id")
  private Long executionRunId;

  @JsonProperty("hybrid_browser_driver_path")
  private String hybridBrowserDriverPath;
  @JsonProperty("chromedriver_executable_dir")
  private String chromedriverExecutableDir;

  @JsonProperty(value = "native_test_object_key")
  private String nativeTestObjectKey;
  @JsonProperty("test_object_api_key")
  private String testObjectApiKey;
  @JsonProperty("run_in_parallel")
  private Boolean runInParallel;
  @JsonProperty(value = "create_session_at_case_level")
  private Boolean createSessionAtCaseLevel;
  @JsonProperty("apksrcType")
  private String androidApkSourceType;
  @JsonProperty("ipasrcType")
  private String iosSourceType;

}
