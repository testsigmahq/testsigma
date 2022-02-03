package com.testsigma.automator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDeviceSettings {
  @NotNull
  private String browser;
  private String browserVersion;
  @NotNull
  private Platform platform;
  private String appiumUrl;
  private String appUrl;
  private AppPathType appPathType;
  private String bundleId;
  private String executionName;
  private Long environmentParamId;
  @JsonProperty(value = "user_name")
  @NotNull
  private String userName;
  @NotNull
  private String password;
  private String runBy;
  private Long envRunId;
  @NotNull
  private String jwtApiKey;
  private Integer elementTimeout;
  private Integer pageLoadTimeout;
  private Long executionRunId;
  private String hybridBrowserDriverPath;
  private String chromedriverExecutableDir;
  @JsonProperty(value = "browser_version_found")
  private String browserVersionFound;
  @JsonProperty(value = "slPath")
  private String screenshotLocalPath;
  private Platform os;
  private String deviceName;
  private String deviceUniqueId;
}
