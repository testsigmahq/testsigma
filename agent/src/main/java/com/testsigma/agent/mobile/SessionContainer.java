/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SessionContainer {
  @Getter
  private final Map<String, RemoteWebDriver> sessionMap = new ConcurrentHashMap<>();

  @Getter
  private final Map<String, String> sessionToDeviceIdMap = new ConcurrentHashMap<>();

  @Getter
  private final Map<String, String> deviceToSessionMap = new ConcurrentHashMap<>();
}
