package com.testsigma.dto.export;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.AppPathType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonListRootName(name = "test-machine-settings")
@JsonRootName(value = "test-machine-settings")
public class EnvironmentSettingsXMLDTO {
  public String title;
  @JsonProperty("os-version")
  private String osVersion;
  @JsonProperty("browser")
  private String browser;
  @JsonProperty("browser-version")
  private String browserVersion;
  @JsonProperty("resolution")
  private String resolution;
  @JsonProperty("platform")
  private String platform;
  @JsonProperty("type")
  private String type;
  @JsonProperty("device-name")
  private String deviceName;
  @JsonProperty("android-apk-source-type")
  private String androidApkSourceType;
  @JsonProperty("ios-source-type")
  private String iosSourceType;
  @JsonProperty("bundle-id")
  private String bundleId;
  @JsonProperty("appium-url")
  private String appiumUrl;
  @JsonProperty("app-upload-id")
  private String appUploadId;
  @JsonProperty("app-package")
  private String appPackage;
  @JsonProperty("app-activity")
  private String appActivity;
  @JsonProperty("app")
  private String app;
  @JsonProperty("apk-path")
  private String apkPath;
  @JsonProperty("capabilities")
  private String capabilities;
  @JsonProperty("app-path-type")
  private AppPathType appPathType;
  @JsonProperty("path-type")
  private String pathType;
  @JsonProperty("app-url")
  private String appUrl;
  @JsonProperty("execution-name")
  private String executionName;
  @JsonProperty("environment-param-id")
  private Long environmentParamId;
  @JsonProperty(value = "user-name")
  private String userName;
  @JsonProperty("password")
  private String password;
  @JsonProperty("run-by")
  private String runBy;
  @JsonProperty("tenant")
  private String tenant;
  @JsonProperty("env-run-id")
  private Long envRunId;
  @JsonProperty("jwt-api-key")
  private String jwtApiKey;
  @JsonProperty(value = "system-title")
  private String systemTitle;
  @JsonProperty(value = "target-machine")
  private Long targetMachine;
  @JsonProperty(value = "host-name")
  private String hostName;
  @JsonProperty("protocol")
  private String protocol;
  @JsonProperty(value = "ip-address")
  private String ipAddress;
  @JsonProperty("port")
  private Integer port;
  @JsonProperty("device-id")
  private Long deviceId;
  @JsonProperty("is-latest")
  private Boolean isLatest;
  @JsonProperty(value = "tenant-id")
  private String tenantId;
  @JsonProperty(value = "wem")
  private String webExecutionModel;
  @JsonProperty(value = "mem")
  private String mobileExecutionModel;
  @JsonProperty("element-timeout")
  private Integer elementTimeout;
  @JsonProperty("pageload-timeout")
  private Integer pageloadTimeout;
  @JsonProperty("execution-run-id")
  private Long executionRunId;
  @JsonProperty("device-name-capability")
  private String deviceNameCapability;
  @JsonProperty("extended-debugging")
  private String extendedDebugging;
  @JsonProperty("hybrid-browser-driver-path")
  private String hybridBrowserDriverPath;
  @JsonProperty("is-dry")
  private String isDry;
  @JsonProperty("skip-server-installation")
  private String skipServerInstallation;
  @JsonProperty("skip-device-initialization")
  private String skipDeviceInitialization;
  @JsonProperty("chrome-driver-executable-dir")
  private String chromedriverExecutableDir;
  @JsonProperty(value = "platform-app-id")
  private String platformAppId;
  @JsonProperty("native-web-screenshot")
  private String nativeWebScreenshot;
  @JsonProperty("uuid")
  private String udid;
  @JsonProperty("grid-url")
  private String gridUrl;
  @JsonProperty(value = "native-test-object-key")
  private String nativeTestObjectKey;
  @JsonProperty("test-object-api-key")
  private String testObjectApiKey;
  @JsonProperty("run-in-parallel")
  private Boolean runInParallel;
  @JsonProperty(value = "create-session-at-case-level")
  private Boolean createSessionAtCaseLevel;
}
