package com.testsigma.schedulers;

import com.testsigma.mapper.ScheduleTestPlanMapper;
import com.testsigma.service.*;
import com.testsigma.util.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestsigmaScheduleService {
  private final WebApplicationContext webApplicationContext;
  private final TestPlanService testPlanService;
  private final WorkspaceService workspaceService;
  private final ScheduleTestPlanService scheduleTestPlanService;
  private final ScheduleTestPlanMapper mapper;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final SchedulerService schedulerService;

  //Runs on 0th second of every 3 minute
  @Scheduled(cron = "0 0/3 * * * *")
  public void minuteJobs() {
    scheduleTestPlans();
  }

  private void scheduleTestPlans() {
    try {
      log.info("Checking For Scheduled Test Plan Executions");
      scheduleTestPlanTask scheduleTestPlanTask = new scheduleTestPlanTask(agentExecutionServiceObjectFactory,
        this.testPlanService, this.workspaceService,
        this.scheduleTestPlanService, this.mapper, this.schedulerService);
      ScheduleExecutionTaskFactory.getInstance().startTask(scheduleTestPlanTask);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}

