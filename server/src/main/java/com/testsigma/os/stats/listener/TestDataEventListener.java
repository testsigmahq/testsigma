package com.testsigma.os.stats.listener;

import com.testsigma.event.EventType;
import com.testsigma.event.TestDataEvent;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.TestData;
import com.testsigma.os.stats.service.TestsigmaOsStatsService;
import com.testsigma.service.TestDataProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestDataEventListener {
  private final TestsigmaOsStatsService testsigmaOsStatsService;
  private final TestDataProfileService testDataProfileService;

  @EventListener(classes = TestDataEvent.class)
  public void OnTestDataEvent(TestDataEvent<TestData> event) {
    log.info("Caught TestDataEvent - " + event);
    try {
      if (event.getEventType() == EventType.CREATE) {
        testsigmaOsStatsService.sendTestDataStats(event.getEventData(), com.testsigma.os.stats.event.EventType.CREATE);
      } else if (event.getEventType() == EventType.DELETE) {
        testsigmaOsStatsService.sendTestDataStats(event.getEventData(), com.testsigma.os.stats.event.EventType.DELETE);
      }else if (event.getEventType() == EventType.UPDATE){
        testsigmaOsStatsService.updateDependencies(event.getEventData().getRenamedColumns(), event.getEventData().getId());
      }
    } catch (TestsigmaException e) {
      log.error(e.getMessage(), e);
    }
  }
}
