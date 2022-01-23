package com.testsigma.agent.tasks;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.http.HttpClient;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.AppBridge;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.suggestion.entity.SuggestionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CloudAppBridge implements AppBridge {

  private final WebAppHttpClient webAppHttpClient;
  private final AgentConfig agentConfig;

  @Override
  public void postEnvironmentResult(EnvironmentRunResult environmentRunResult) throws AutomatorException {
    try {
      String endpointUrl = ServerURLBuilder.environmentResultURL(environmentRunResult.getId());
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      log.info("Sending environment run results to - " + endpointUrl);
      HttpResponse<String> response = webAppHttpClient.put(endpointUrl, environmentRunResult, null, authHeader);
      log.debug("Sent environment run results to cloud servers successfully - "
        + response.getStatusCode() + " - " + response.getResponseEntity());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void postTestSuiteResult(TestSuiteResult testSuiteResult) throws AutomatorException {
    try {
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      webAppHttpClient.put(ServerURLBuilder.testSuiteResultURL(testSuiteResult.getId()), testSuiteResult,
        null, authHeader);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void postTestCaseResult(TestCaseResult testCaseResult) throws AutomatorException {
    try {
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      webAppHttpClient.put(ServerURLBuilder.testCaseResultURL(testCaseResult.getId()), testCaseResult, null,
        authHeader);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateEnvironmentResultData(TestDeviceResultRequest testDeviceResultRequest) throws AutomatorException {
    try {
      String url = ServerURLBuilder.environmentResultUpdateURL(testDeviceResultRequest.getId());
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<String> response = webAppHttpClient.put(url, testDeviceResultRequest, new TypeReference<>() {
      }, authHeader);
      log.info(response.getStatusCode() + " - " + response.getResponseText());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateTestSuiteResultData(TestSuiteResultRequest testSuiteResultRequest) throws AutomatorException {
    try {
      String url = ServerURLBuilder.testSuiteResultUpdateURL(testSuiteResultRequest.getId());
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<String> response = webAppHttpClient.put(url, testSuiteResultRequest, new TypeReference<>() {
      }, authHeader);
      log.error(response.getStatusCode() + " - " + response.getResponseText());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateTestCaseResultData(TestCaseResultRequest testCaseResultRequest) throws AutomatorException {
    try {
      String url = ServerURLBuilder.testCaseResultUpdateURL(testCaseResultRequest.getId());
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<String> response = webAppHttpClient.put(url, testCaseResultRequest, new TypeReference<>() {
      }, authHeader);
      log.error(response.getStatusCode() + " - " + response.getResponseText());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public TestCaseEntity getTestCase(Long environmentResultId, TestCaseEntity testCaseEntity) throws AutomatorException {
    try {
      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

      if (StringUtils.isNotBlank(testCaseEntity.getTestDataSetName())) {
        queryParams.add("testDataSetName", testCaseEntity.getTestDataSetName());
      }
      queryParams.add("testCaseResultId", testCaseEntity.getTestCaseResultId().toString());
      queryParams.add("environmentResultId", environmentResultId.toString());
      String url = ServerURLBuilder.testCaseDetailsURL(testCaseEntity.getId(), queryParams);
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<TestCaseEntity> response = webAppHttpClient.get(url, new TypeReference<>() {
      }, authHeader);
      if (response.getStatusCode() > 200) {
        log.error("---------------- Error while fetching test case - " + response.getStatusCode());
      }
      return response.getResponseEntity();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateElement(String name, ElementRequestEntity elementRequestEntity) throws AutomatorException {
    try {
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<ElementEntity> response =
        webAppHttpClient.put(ServerURLBuilder.elementURL(name), elementRequestEntity, new TypeReference<>() {
        }, authHeader);
      log.info("Element update response - " + response);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public String getRunTimeData(String variableName, Long environmentResultId, String sessionId) throws AutomatorException {
    try {
      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.add("environmentResultId", environmentResultId.toString());
      queryParams.add("sessionId", sessionId);
      String url = ServerURLBuilder.runTimeDataURL(variableName, queryParams);
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      HttpResponse<String> response = webAppHttpClient.get(url, new TypeReference<>() {
      }, authHeader);
      if (response.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
        throw new AutomatorException(AutomatorMessages.getMessage(AutomatorMessages.EXCEPTION_INVALID_TESTDATA,
          variableName));
      }
      return response.getResponseEntity();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateRunTimeData(Long environmentResultId, RuntimeEntity runtimeEntity) throws AutomatorException {
    try {
      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.add("environmentResultId", environmentResultId.toString());
      String url = ServerURLBuilder.runTimeNewDataURL(queryParams);

      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      webAppHttpClient.put(url, runtimeEntity, null, authHeader);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public WebDriverSettingsDTO getWebDriverSettings(Long environmentResultId) throws AutomatorException {
    try {
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      String url = ServerURLBuilder.capabilitiesURL(environmentResultId);
      HttpResponse<WebDriverSettingsDTO> response = webAppHttpClient.get(url, new TypeReference<>() {
      }, authHeader);
      if (response.getStatusCode() != HttpStatus.OK.value()) {
        throw new AutomatorException(response.getStatusMessage());
      }
      return response.getResponseEntity();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public String getDriverExecutablePath(String browserName, String browserVersion)
    throws AutomatorException {
    try {
      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.add("browserName", browserName);
      queryParams.add("browserVersion", browserVersion);
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      String url = ServerURLBuilder.driverExecutableURL(queryParams, agentConfig.getUUID());
      HttpResponse<String> response = webAppHttpClient.get(url, new TypeReference<>() {
      }, authHeader);
      if (response.getStatusCode() != HttpStatus.OK.value()) {
        throw new AutomatorException(response.getStatusMessage());
      }
      return response.getResponseEntity();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public List<SuggestionEntity> getSuggestions(Integer naturalTextActionId) throws AutomatorException {
    try {
      String authHeader = HttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      String url = ServerURLBuilder.suggestionsURL(naturalTextActionId);
      HttpResponse<List<SuggestionEntity>> response = webAppHttpClient.get(url, new TypeReference<>() {
      }, authHeader);
      if (response.getStatusCode() != HttpStatus.OK.value()) {
        throw new AutomatorException(response.getStatusMessage());
      }
      return response.getResponseEntity();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }
}
