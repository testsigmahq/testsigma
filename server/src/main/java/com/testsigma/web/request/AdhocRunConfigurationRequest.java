package com.testsigma.web.request;

import com.testsigma.model.WorkspaceType;
import lombok.Data;

@Data
public class AdhocRunConfigurationRequest {
  private Long id;
  private String configName;
  private Integer pageTimeOut;
  private Integer elementTimeOut;
  private WorkspaceType workspaceType;
  private Long type;
  private String browser;
  private Long platformOsVersionId;
  private Long platformBrowserVersionId;
  private Long platformScreenResolutionId;
  private Long platformDeviceId;
  private String environmentId;
  private Long captureScreenshots;
  private String desiredCapabilities;
  private String machine;
  private String appName;
  private String udId;
  private String appPackage;
  private String appActivity;
  private String appUploadId;
  private String appUrl;
  private String appBundleId;
  private Long deviceId;
  private Long agentId;
  private String appPathType;
}
