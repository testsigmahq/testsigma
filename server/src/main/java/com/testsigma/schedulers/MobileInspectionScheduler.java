package com.testsigma.schedulers;

import com.testsigma.mapper.MobileInspectionMapper;
import com.testsigma.model.MobileInspection;
import com.testsigma.model.MobileInspectionStatus;
import com.testsigma.service.MobileInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MobileInspectionScheduler {

  private final MobileInspectionService mobileInspectionService;
  private final MobileInspectionMapper mobileInspectionMapper;

  //Runs every 3 minutes
  @Scheduled(cron = "0 0/3 * * * *")
  private void stopNonActiveSessions() throws Exception {
    log.info("Scheduler: Checking for non active mobile inspections");
    Timestamp expiryTime = new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2));
    List<MobileInspectionStatus> statusTypes = Arrays.asList(MobileInspectionStatus.TRIGGERED, MobileInspectionStatus.STARTED);
    List<MobileInspection> nonActiveSessions = mobileInspectionService.findAllByLastActiveAtBeforeAndStatusIn(
      expiryTime, statusTypes);
    for (MobileInspection mobileInspection : nonActiveSessions) {
      try {
        log.info("Closing mobile inspection session with session id: " + mobileInspection.getSessionId());
        if (((mobileInspection.getStatus() == MobileInspectionStatus.TRIGGERED) &&
          (System.currentTimeMillis() - mobileInspection.getLastActiveAt().getTime()) > (30 * 60 * 1000))
          || ((mobileInspection.getStatus() == MobileInspectionStatus.STARTED) &&
          (System.currentTimeMillis() - mobileInspection.getLastActiveAt().getTime()) > (2 * 60 * 1000))) {
          mobileInspectionService.closeSession(mobileInspection.getId());
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

}
