/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.entity;

import com.testsigma.automator.drivers.WebDriverCapability;
import lombok.Data;

import java.net.URL;
import java.util.List;

@Data
public class WebDriverSettingsDTO {
  private List<WebDriverCapability> webDriverCapabilities;
  private URL webDriverServerUrl;
}
