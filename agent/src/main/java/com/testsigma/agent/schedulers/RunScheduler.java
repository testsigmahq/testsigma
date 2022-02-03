package com.testsigma.agent.schedulers;

import com.testsigma.agent.dto.ExecutionDTO;
import com.testsigma.agent.tasks.TestPlanRunTask;
import com.testsigma.agent.http.HttpClient;
import com.testsigma.agent.http.ServerURLBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.automator.exceptions.AgentDeletedException;
import com.testsigma.automator.http.HttpResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Log4j2
public class RunScheduler extends BaseScheduler {
  @Autowired
  public RunScheduler(WebApplicationContext webApplicationContext) {
    super(webApplicationContext);
  }

  @Scheduled(cron = "${agent.jobs.runSchedule:-}")
  public void run() {
    try {
      Thread.currentThread().setName("RunScheduler");

      if (skipScheduleRun()) {
        log.info("Skipping agent RunScheduler run...");
        return;
      }

      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<ExecutionDTO> response =
        httpClient.get(ServerURLBuilder.executionURL(agentConfig.getUUID()), new TypeReference<>() {
        }, authHeader);
      if (response.getStatusCode() == HttpStatus.OK.value()) {
        ExecutionDTO executionDTO = response.getResponseEntity();
        setRequestId(response);
        startExecutions(
          executionDTO.getEnvironment()
        );
      } else {
        log.error("Unable To Fetch Executions From Testsigma Servers. Request Failed With Response Code - "
          + response.getStatusCode());
      }
    } catch (AgentDeletedException e) {
      deRegisterAgent(e);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void startExecutions(TestDeviceEntity testDeviceEntity) {
    if (testDeviceEntity != null) {
      log.info("The EnvironmentEntity - " + testDeviceEntity.getId() + " executions returned from servers....");
      TestPlanRunTask task = new TestPlanRunTask(testDeviceEntity);
      task.setName("ExecutionTask - Environment Result ID [" + testDeviceEntity.getEnvironmentResultId() + "]");
      task.setWebApplicationContext(webApplicationContext);
      task.start();
    } else {
      log.info("There are no executions in queue....");
    }
  }

  private void setRequestId(HttpResponse<ExecutionDTO> response) {
    if (response.getResponseHeaders().length != 0) {
      Header[] headers = response.getResponseHeaders();
      for (Header header : headers) {
        if ("X-Request-Id".equals(header.getName())) {
          ThreadContext.put("X-Request-Id", header.getValue());
        }
      }
    }
  }
}
