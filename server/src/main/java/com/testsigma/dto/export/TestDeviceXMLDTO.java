/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.AppPathType;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonListRootName(name = "test-devices")
@JsonRootName(value = "test-devices")
public class TestDeviceXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("test-plan-id")
  private Long testPlanId;
  @JsonProperty("title")
  private String title;
  @JsonProperty("agent-id")
  private Long agentId;
  @JsonProperty("device-id")
  private Long deviceId;
  @JsonProperty("browser")
  private String browser;
  @JsonProperty("platform-os-version-id")
  private Long platformOsVersionId;
  @JsonProperty("platform-browser-version-id")
  private Long platformBrowserVersionId;
  @JsonProperty("platform-screen-resolution-id")
  private Long platformScreenResolutionId;
  @JsonProperty("platform-device-id")
  private Long platformDeviceId;
  @JsonProperty("udid")
  private String udid;
  @JsonProperty("app-upload-id")
  private String appUploadId;
  @JsonProperty("app-package")
  private String appPackage;
  @JsonProperty("app-activity")
  private String appActivity;
  @JsonProperty("app-url")
  private String appUrl;
  @JsonProperty("app-bundle-id")
  private String appBundleId;
  @JsonProperty("app-path-type")
  private AppPathType appPathType;
  @JsonProperty("capabilities")
  private String capabilities;
  @JsonProperty("disable")
  private Boolean disable = false;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("match-browser-version")
  private Boolean matchBrowserVersion = false;
  @JsonProperty("run-in-parallel")
  private Boolean runInParallel = Boolean.FALSE;
  @JsonProperty("create-session-at-case-level")
  private Boolean createSessionAtCaseLevel = Boolean.FALSE;
  @JsonProperty("env-parameters-name")
  private String envParametersName;
  @JsonProperty("suite-ids")
  private List<Long> suiteIds;
}
