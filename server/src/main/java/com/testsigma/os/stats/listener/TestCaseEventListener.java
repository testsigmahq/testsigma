package com.testsigma.os.stats.listener;

import com.testsigma.event.EventType;
import com.testsigma.event.TestCaseEvent;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.TestCase;
import com.testsigma.os.stats.service.TestsigmaOsStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCaseEventListener {
  private final TestsigmaOsStatsService testsigmaOsStatsService;

  @EventListener(classes = TestCaseEvent.class)
  public void OnTestCaseEvent(TestCaseEvent<TestCase> event) {
    log.info("Caught TestCaseEvent - " + event);
    try {
      if (event.getEventType() == EventType.CREATE) {
        testsigmaOsStatsService.sendTestCaseStats(event.getEventData(), com.testsigma.os.stats.event.EventType.CREATE);
      } else if (event.getEventType() == EventType.DELETE) {
        testsigmaOsStatsService.sendTestCaseStats(event.getEventData(), com.testsigma.os.stats.event.EventType.DELETE);
      }
    } catch (TestsigmaException e) {
      log.error(e.getMessage(), e);
    }
  }
}
