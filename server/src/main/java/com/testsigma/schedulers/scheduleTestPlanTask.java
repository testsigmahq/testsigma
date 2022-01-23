package com.testsigma.schedulers;

import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.ScheduleTestPlanMapper;
import com.testsigma.model.*;
import com.testsigma.service.AgentExecutionService;
import com.testsigma.service.WorkspaceService;
import com.testsigma.service.ScheduleTestPlanService;
import com.testsigma.service.TestPlanService;
import com.testsigma.util.SchedulerService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Log4j2
@Data
public class scheduleTestPlanTask implements Runnable {
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final TestPlanService testPlanService;
  private final WorkspaceService workspaceService;
  private final ScheduleTestPlanService scheduleTestPlanService;
  private final ScheduleTestPlanMapper mapper;
  private final SchedulerService schedulerService;


  public scheduleTestPlanTask(
    ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory,
    TestPlanService testPlanService,
    WorkspaceService workspaceService,
    ScheduleTestPlanService scheduleTestPlanService,
    ScheduleTestPlanMapper mapper, SchedulerService schedulerService) {
    super();
    this.agentExecutionServiceObjectFactory = agentExecutionServiceObjectFactory;
    this.testPlanService = testPlanService;
    this.workspaceService = workspaceService;
    this.scheduleTestPlanService = scheduleTestPlanService;
    this.mapper = mapper;
    this.schedulerService = schedulerService;
  }

  public void run() {
    try {
      this.runSchedules();
    } catch (TestsigmaException | SQLException e) {
      log.error(e.getMessage(), e);
    }
  }

  public ResponseEntity<Object> runSchedules() throws TestsigmaException, SQLException {
    Timestamp currentTime = getCurrentUTCTime();
    String message = null;
    Set<Long> runningExecutionIds = new HashSet<Long>();
    try {
      List<ScheduleTestPlan> scheduleTestPlanList = this.scheduleTestPlanService.findAllActiveSchedules(currentTime);
      log.info("Found " + scheduleTestPlanList.size() + " scheduled test plans");

      for (ScheduleTestPlan schedule : scheduleTestPlanList) {
        schedule.setQueueStatus(ScheduleQueueStatus.IN_PROGRESS);
        this.scheduleTestPlanService.update(schedule);
      }
      for (ScheduleTestPlan schedule : scheduleTestPlanList) {
        if (runningExecutionIds.contains(schedule.getTestPlanId())) {
          log.info("Scheduled test plan - " + schedule.getName() + " already running. Skipping it....");
          continue;
        }
        try {
          log.info("Triggering scheduled test plan - " + schedule.getName() + "....");
          TestPlan testPlan = this.testPlanService.find(schedule.getTestPlanId());
          AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
          agentExecutionService.setTestPlan(testPlan);
          String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
          ThreadContext.put("X-Request-Id", uuid);
          ThreadContext.put("NewRelic:X-Request-Id", uuid);
          agentExecutionService.setTriggeredType(ExecutionTriggeredType.SCHEDULED);
          agentExecutionService.setScheduleId(schedule.getId());
          agentExecutionService.start();
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
        runningExecutionIds.add(schedule.getTestPlanId());

        if (!schedule.getScheduleType().equals(ScheduleType.ONCE)) {
          schedule.setStatus(ScheduleStatus.ACTIVE);
          populateNextInterval(schedule);
        } else {
          schedule.setStatus(ScheduleStatus.IN_ACTIVE);
        }

        schedule.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        schedule.setQueueStatus(ScheduleQueueStatus.COMPLETED);
        this.scheduleTestPlanService.update(schedule);
      }
      return new ResponseEntity<>(message, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e, e);
      Thread.currentThread().interrupt();

      return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private Timestamp getCurrentUTCTime() {
    ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
    ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
    return Timestamp.valueOf(utc.toLocalDateTime());
  }

  private void populateNextInterval(ScheduleTestPlan scheduleTestPlan) throws TestsigmaException {
    //[TODO] [Pratheepv] Bad way to handle Need to revisit this one
    try {
      Timestamp scheduleTime = schedulerService.getScheduleTime(scheduleTestPlan.getScheduleType(), scheduleTestPlan.getScheduleTime());
      scheduleTestPlan.setScheduleTime(scheduleTime);
    } catch (ParseException e) {
      e.printStackTrace();
      throw new TestsigmaException("Problem while calculating next interval");
    }
  }
}
