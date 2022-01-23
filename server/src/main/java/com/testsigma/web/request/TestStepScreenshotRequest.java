/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import lombok.Data;

@Data
public class TestStepScreenshotRequest {
  Long id;
  Long testStepId;
  Long testStepResultId;
  Long testCaseResultId;
  Long environmentResultId;
  String ignoredCoordinates;
  String baseImageName;
  String screenResolution;
  String browser;
  Double browserVersion;
  String deviceName;
  String deviceOsVersion;
}
