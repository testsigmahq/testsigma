/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.init;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

@Log4j2
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (event instanceof ApplicationPreparedEvent) {
      new ApplicationEventHandler().handleStartEvent();
    } else if (event instanceof ContextClosedEvent) {
      new ApplicationEventHandler().handleShutdownEvent();
    } else if (event instanceof ContextRefreshedEvent) {
      new ApplicationEventHandler().postAppContextReadyActions(((ContextRefreshedEvent) event).getApplicationContext());
    } else if (event instanceof WebServerInitializedEvent) {
      new ApplicationEventHandler().runPostWebContextReadyActions((WebServerInitializedEvent) event);
    }
  }


}
