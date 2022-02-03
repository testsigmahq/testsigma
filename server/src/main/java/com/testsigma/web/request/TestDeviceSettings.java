package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.AppPathType;
import com.testsigma.model.Platform;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TestDeviceSettings {
  @NotNull(message = "title is required")
  public String title;
  @NotNull(message = "osVersion is required")
  private String osVersion;
  @NotNull(message = "browser is required")
  private String browser;
  @NotNull(message = "browserVersion is required")
  private String browserVersion;
  @NotNull(message = "resolution is required")
  private String resolution;
  @NotNull(message = "platform is required")
  private Platform platform;
  @NotNull(message = "type is required")
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

}
