/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.schedulers;

import com.testsigma.automator.exceptions.AgentDeletedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Log4j2
public class AgentDetailsScheduler extends BaseScheduler {
  @Autowired
  public AgentDetailsScheduler(WebApplicationContext webApplicationContext) {
    super(webApplicationContext);
  }

  @Scheduled(cron = "${agent.jobs.agentDetailsSchedule:-}")
  public void run() {
    try {
      Thread.currentThread().setName("AgentDetailsScheduler");
      if (skipScheduleRun()) {
        log.info("Skipping agent AgentDetailsScheduler run...");
        return;
      }
      log.debug("Syncing browser details");
      this.agentBrowserService.initialise();
      this.agentBrowserService.sync();
    } catch (AgentDeletedException e) {
      deRegisterAgent(e);
    } catch (Exception e) {
      log.error(e.getMessage(), e);

    }
  }
}
