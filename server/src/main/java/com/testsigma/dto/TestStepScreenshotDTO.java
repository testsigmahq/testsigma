/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import lombok.Data;

@Data
public class TestStepScreenshotDTO {
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
  String screenShotURL;
}
