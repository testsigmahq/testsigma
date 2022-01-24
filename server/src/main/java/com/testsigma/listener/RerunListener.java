package com.testsigma.listener;

import com.testsigma.event.TestPlanResultEvent;
import com.testsigma.model.AbstractTestPlan;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestPlanResult;
import com.testsigma.service.TestPlanResultService;
import com.testsigma.service.TestPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RerunListener {
  private final TestPlanResultService testPlanResultService;
  private final TestPlanService testPlanService;

  @EventListener(classes = TestPlanResultEvent.class)
  public void onExecutionCompleted(TestPlanResultEvent<TestPlanResult> event) {
    TestPlanResult testPlanResult = event.getEventData();
    if (testPlanResult.getStatus() == StatusConstant.STATUS_COMPLETED) {
      try {
        AbstractTestPlan execution = testPlanResult.getTestPlan();
        if (execution == null)
          return;
        log.info(String.format("Starting re-run for test plan %s with test plan result %s", execution.getId(),
          testPlanResult.getId()));
        testPlanResultService.rerun(execution, testPlanResult);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    } else {
      log.info(String.format("Test Plan Result [%s] is not completed. Skipping re-run", testPlanResult.getId()));
    }
  }
}
