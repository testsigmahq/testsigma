/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator;

import com.testsigma.automator.utilities.PathUtil;
import lombok.Data;

@Data
public class AutomatorConfig {
  private static AutomatorConfig _instance = null;

  private String cloudServerUrl;
  private int testCaseFetchWaitInterval;
  private int testCaseDefaultMaxTries;
  private AppBridge appBridge;

  public static AutomatorConfig getInstance() {
    if (_instance == null) {
      _instance = new AutomatorConfig();
    }
    return _instance;
  }

  public void init() {
    PathUtil.getInstance().setPathsFromContext();
  }

}
