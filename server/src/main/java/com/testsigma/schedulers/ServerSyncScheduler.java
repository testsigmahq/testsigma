package com.testsigma.schedulers;

import com.testsigma.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServerSyncScheduler {

  private final ServerService serverService;

  @Scheduled(fixedRate = 12 * 60 * 60 * 1000, initialDelay = 2 * 60 * 1000) //Every 12 hours
  private void syncServer() {
    log.info("Scheduler: Sync server details");
    try {
      serverService.syncServer();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
