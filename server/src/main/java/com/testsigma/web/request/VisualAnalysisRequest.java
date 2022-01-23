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
public class VisualAnalysisRequest {
  private String baseImagePath;
  private String currentRunScreenshotPath;
  private String action;
  private String ignoreCoordinates;
  private Long screenshotResultId;

}
