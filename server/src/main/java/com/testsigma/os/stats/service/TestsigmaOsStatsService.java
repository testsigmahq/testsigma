package com.testsigma.os.stats.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import com.testsigma.os.stats.config.UrlConstants;
import com.testsigma.os.stats.entity.*;
import com.testsigma.os.stats.event.EventType;
import com.testsigma.service.*;
import com.testsigma.tasks.TestDataParameterUpdateTaskHandler;
import com.testsigma.util.HttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestsigmaOsStatsService {
  private final HttpClient httpClient;
  private final WorkspaceVersionService workspaceVersionService;
  private final WorkspaceService workspaceService;
  private final DryTestPlanService dryTestPlanService;
  private final TestPlanService testPlanService;
  private final TestsigmaOSConfigService testsigmaOSConfigService;
  private final ServerService serverService;
  private final TestStepService testStepService;
  private final TestDataParameterUpdateTaskHandler testDataParameterUpdateTaskHandler;

  public void sendTestCaseStats(TestCase testCase, EventType eventType) throws TestsigmaException {
    TestCaseStatEntity testCaseStatEntity = new TestCaseStatEntity();
    Server server = serverService.findOne();
    testCaseStatEntity.setEventType(eventType);
    testCaseStatEntity.setTestCaseId(testCase.getId());
    testCaseStatEntity.setServerUuid(server.getServerUuid());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_TEST_CASE_STATS_URL, getHeaders(), testCaseStatEntity, new TypeReference<String>() {
    });
  }

  public void sendTestSuiteStats(TestSuite testSuite, EventType eventType) throws TestsigmaException {
    TestSuiteStatEntity testSuiteStatEntity = new TestSuiteStatEntity();
    Server server = serverService.findOne();
    testSuiteStatEntity.setServerUuid(server.getServerUuid());
    testSuiteStatEntity.setEventType(eventType);
    testSuiteStatEntity.setTestSuiteId(testSuite.getId());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_TEST_SUITE_STATS_URL, getHeaders(), testSuiteStatEntity, new TypeReference<String>() {
    });
  }

  public void sendTestStepStats(TestStep testStep, EventType eventType) throws TestsigmaException {
    TestStepStatEntity testStepStatEntity = new TestStepStatEntity();
    Server server = serverService.findOne();
    testStepStatEntity.setServerUuid(server.getServerUuid());
    testStepStatEntity.setEventType(eventType);
    testStepStatEntity.setTestStepId(testStep.getId());
    testStepStatEntity.setTestCaseId(testStep.getTestCaseId());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_TEST_STEP_STATS_URL, getHeaders(), testStepStatEntity, new TypeReference<String>() {
    });
  }

  public void sendTestDataStats(TestData testData, EventType eventType) throws TestsigmaException {
    TestDataStatEntity testDataStatEntity = new TestDataStatEntity();
    Server server = serverService.findOne();
    testDataStatEntity.setServerUuid(server.getServerUuid());
    testDataStatEntity.setEventType(eventType);
    testDataStatEntity.setTestDataId(testData.getId());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_TEST_DATA_STATS_URL, getHeaders(), testDataStatEntity, new TypeReference<String>() {
    });
  }

  public void sendElementStats(Element element, EventType eventType) throws TestsigmaException {
    ElementStatEntity elementStatEntity = new ElementStatEntity();
    Server server = serverService.findOne();
    elementStatEntity.setServerUuid(server.getServerUuid());
    elementStatEntity.setEventType(eventType);
    elementStatEntity.setElementId(element.getId());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_ELEMENT_STATS_URL, getHeaders(), elementStatEntity, new TypeReference<String>() {
    });
  }

  public void sendEnvironmentStats(Environment environment, EventType eventType) throws TestsigmaException {
    EnvironmentStatEntity environmentStatEntity = new EnvironmentStatEntity();
    Server server = serverService.findOne();
    environmentStatEntity.setServerUuid(server.getServerUuid());
    environmentStatEntity.setEventType(eventType);
    environmentStatEntity.setEnvironmentId(environment.getId());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_ENVIRONMENT_STATS_URL, getHeaders(), environmentStatEntity, new TypeReference<String>() {
    });
  }

  public void sendUploadStats(Upload upload, EventType eventType) throws TestsigmaException {
    UploadStatEntity uploadStatEntity = new UploadStatEntity();
    Server server = serverService.findOne();
    uploadStatEntity.setServerUuid(server.getServerUuid());
    uploadStatEntity.setEventType(eventType);
    uploadStatEntity.setUploadId(upload.getId());
    uploadStatEntity.setUploadExtension(FilenameUtils.getExtension(upload.getFileName()));
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_UPLOAD_STATS_URL, getHeaders(), uploadStatEntity, new TypeReference<String>() {
    });
  }

  public void sendTestPlanStats(TestPlan testPlan, EventType eventType) throws TestsigmaException {
    TestPlanStatEntity testPlanStatEntity = new TestPlanStatEntity();
    Server server = serverService.findOne();
    testPlanStatEntity.setServerUuid(server.getServerUuid());
    testPlanStatEntity.setEventType(eventType);
    testPlanStatEntity.setTestPlanId(testPlan.getId());
    testPlanStatEntity.setTestPlanLabType(testPlan.getTestPlanLabType());
    testPlanStatEntity.setEntityType(testPlan.getEntityType());
    WorkspaceVersion applicationVersion = workspaceVersionService.find(testPlan.getWorkspaceVersionId());
    Workspace workspace = workspaceService.find(applicationVersion.getWorkspaceId());
    testPlanStatEntity.setApplicationType(workspace.getWorkspaceType());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_TEST_PLAN_STATS_URL, getHeaders(), testPlanStatEntity, new TypeReference<String>() {
    });
  }

  public void sendTestPlanRunStats(TestPlanResult testPlanRun, EventType eventType) throws TestsigmaException {
    TestPlanRunStatEntity testPlanRunStatEntity = new TestPlanRunStatEntity();
    Server server = serverService.findOne();
    testPlanRunStatEntity.setServerUuid(server.getServerUuid());
    testPlanRunStatEntity.setEventType(eventType);
    testPlanRunStatEntity.setTestPlanRunId(testPlanRun.getId());
    AbstractTestPlan testPlan = testPlanService.findById(testPlanRun.getTestPlanId());
    if (testPlan == null) {
      testPlan = dryTestPlanService.find(testPlanRun.getTestPlanId());
    }
    WorkspaceVersion applicationVersion = workspaceVersionService.find(testPlan.getWorkspaceVersionId());
    Workspace workspace = workspaceService.find(applicationVersion.getWorkspaceId());
    testPlanRunStatEntity.setTestPlanLabType(testPlan.getTestPlanLabType());
    testPlanRunStatEntity.setApplicationType(workspace.getWorkspaceType());
    testPlanRunStatEntity.setTestPlanType(testPlan.getEntityType());

    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_TEST_PLAN_RUN_STATS_URL, getHeaders(), testPlanRunStatEntity, new TypeReference<String>() {
    });
  }

  public void sendAgentStats(Agent agent, EventType eventType) throws TestsigmaException {
    AgentStatEntity agentStatEntity = new AgentStatEntity();
    Server server = serverService.findOne();
    agentStatEntity.setServerUuid(server.getServerUuid());
    agentStatEntity.setEventType(eventType);
    agentStatEntity.setAgentId(agent.getId());
    agentStatEntity.setAgentOs(agent.getOsType());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_AGENT_STATS_URL, getHeaders(), agentStatEntity, new TypeReference<String>() {
    });
  }

  public void sendAgentDeviceStats(AgentDevice agentDevice, EventType eventType) throws TestsigmaException {
    AgentDeviceStatEntity agentDeviceStatEntity = new AgentDeviceStatEntity();
    Server server = serverService.findOne();
    agentDeviceStatEntity.setServerUuid(server.getServerUuid());
    agentDeviceStatEntity.setEventType(eventType);
    agentDeviceStatEntity.setAgentDeviceId(agentDevice.getId());
    agentDeviceStatEntity.setAgentDeviceOs(agentDevice.getOsName());
    httpClient.post(testsigmaOSConfigService.getUrl() +
      UrlConstants.TESTSIGMA_OS_AGENT_DEVICE_STATS_URL, getHeaders(), agentDeviceStatEntity, new TypeReference<String>() {
    });
  }

  private ArrayList<Header> getHeaders() {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    return headers;
  }


  public void updateDependencies(Map<String, String> renamedColumns, Long id) {
    if(renamedColumns == null){
      return;
    }
    testDataParameterUpdateTaskHandler.startTask(() -> {
      try{
        renamedColumns.forEach((oldName, newName) ->
          testStepService.updateTestDataParameterName(id, oldName, newName));
      }catch (Exception e){
        log.error(e,e);
      }
    });
  }
}
