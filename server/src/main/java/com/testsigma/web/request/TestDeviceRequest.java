/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.testsigma.model.AppPathType;
import com.testsigma.model.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class TestDeviceRequest {
  Long id;
  Long agentId;
  String title;
  Long deviceId;
  Long platformOsVersionId;
  Long platformBrowserVersionId;
  Long platformScreenResolutionId;
  Long platformDeviceId;
  TestDeviceSettings settings;
  String browser;
  Platform platform;
  String browserVersion;
  String udid;
  Long appUploadId;
  Long appUploadVersionId;
  String appPackage;
  String appActivity;
  String appUrl;
  String appBundleId;
  AppPathType appPathType;
  String capabilities;
  Long envRunId;
  Boolean disable;
  Boolean matchBrowserVersion = false;
  Boolean createSessionAtCaseLevel = false;
  List<Long> suiteIds;
}
