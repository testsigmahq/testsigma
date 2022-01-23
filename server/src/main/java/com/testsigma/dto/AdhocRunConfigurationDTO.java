package com.testsigma.dto;


import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AdhocRunConfigurationDTO {
  private Long id;
  private String configName;
  private Integer pageTimeOut;
  private Integer elementTimeOut;
  private WorkspaceType workspaceType;
  private Long type;
  private String browser;
  private String environmentId;
  private Long captureScreenshots;
  private String desiredCapabilities;
  private Long platformOsVersionId;
  private Long platformBrowserVersionId;
  private Long platformScreenResolutionId;
  private Long platformDeviceId;
  private Long agentId;
  private String appName;
  private String deviceName;
  private String udId;
  private String appPackage;
  private String appActivity;
  private String appUploadId;
  private EnvironmentDTO environment;
  private String appUrl;
  private String appBundleId;
  private Long deviceId;
  private String appPathType;
  private Timestamp createdDate;
  private Timestamp updatedDate;

}
