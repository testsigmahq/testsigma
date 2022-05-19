/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;


import com.google.api.gax.rpc.PagedListDescriptor;
import com.testsigma.model.AppPathType;
import com.testsigma.model.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class TestDeviceDTO {
  private Long id;
  private Long testPlanId;
  private String title;
  private Long agentId;
  private Long platformOsVersionId;
  private Long platformBrowserVersionId;
  private Long platformScreenResolutionId;
  private Long platformDeviceId;
  private String browser;
  private Platform platform;
  private String browserVersion;
  private String udid;
  private String appUploadId;
  private Long appUploadVersionId;
  private String appPackage;
  private String appActivity;
  private String appUrl;
  private String appBundleId;
  private AppPathType appPathType;
  private String capabilities;
  private Boolean disable;
  private Boolean matchBrowserVersion;
  private Long deviceId;
  private Boolean createSessionAtCaseLevel;
}
