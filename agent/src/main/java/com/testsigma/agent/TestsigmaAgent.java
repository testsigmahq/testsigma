/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent;

import com.testsigma.agent.init.WrapperConnector;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableScheduling
@EnableWebMvc
@SpringBootApplication(scanBasePackages = {"com.testsigma.agent", "com.testsigma.automator"})
@Log4j2
public class TestsigmaAgent {
  public static void main(String[] args) {
    System.setProperty("webdriver.http.factory", "jdk-http-client");
    String wrapperPort = System.getProperty("agent.wrapper.port");
    if (StringUtils.isNotBlank(wrapperPort)) {
      WrapperConnector.getInstance().disconnectHook();

    }
    Thread.currentThread().setName("TestsigmaAgent");
    ConfigurableApplicationContext c = SpringApplication.run(TestsigmaAgent.class, args);
    if (StringUtils.isNotBlank(wrapperPort)) {
      WrapperConnector.getInstance().connect();
    }
  }
}
