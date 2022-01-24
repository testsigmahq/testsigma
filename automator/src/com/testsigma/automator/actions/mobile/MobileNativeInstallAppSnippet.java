/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import lombok.extern.log4j.Log4j2;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA;

@Log4j2
public class MobileNativeInstallAppSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "App Installed successfully";

  @Override
  public void execute() throws Exception {
    String downloadURL = getTestDataPropertiesEntity(TEST_STEP_DATA_MAP_KEY_TEST_DATA).getTestDataValuePreSignedURL();
    downloadURL = (downloadURL == null) ? getTestData() : downloadURL;
    getDriver().installApp(downloadURL);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
