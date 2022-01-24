package com.testsigma.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testsigma.dto.SuggestionDTO;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.mapper.SuggestionMapper;
import com.testsigma.service.*;
import com.testsigma.automator.AppBridge;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.suggestion.entity.SuggestionEntity;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.EnvironmentRunResultRequest;
import com.testsigma.web.request.RuntimeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StandaloneAppBridge implements AppBridge {

  private final TestDeviceResultService testDeviceResultService;
  private final TestCaseResultService testCaseResultService;
  private final TestSuiteResultService testSuiteResultService;
  private final TestCaseService testCaseService;

  private final ElementService elementService;
  private final RunTimeDataService runTimeDataService;

  private final WebDriverSettingsService webDriverSettingsService;

  private final SuggestionMappingService suggestionMappingService;

  private final SuggestionMapper suggestionMapper;

  private <T> T convertToObject(Object source, Class<T> destination) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    return objectMapper.readValue(objectMapper.writeValueAsString(source), destination);

  }

  @Override
  public void postEnvironmentResult(EnvironmentRunResult environmentResult) throws AutomatorException {
    try {
      testDeviceResultService.updateResult(convertToObject(environmentResult, EnvironmentRunResultRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void postTestSuiteResult(TestSuiteResult testSuiteResult) throws AutomatorException {
    try {
      testSuiteResultService.updateResult(convertToObject(testSuiteResult,
        com.testsigma.web.request.TestSuiteResultRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void postTestCaseResult(TestCaseResult testCaseResult) throws AutomatorException {
    try {
      testCaseResultService.updateResult(convertToObject(testCaseResult,
        com.testsigma.web.request.TestCaseResultRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateEnvironmentResultData(TestDeviceResultRequest testDeviceResultRequest) throws AutomatorException {
    try {
      testDeviceResultService.updateResultData(convertToObject(testDeviceResultRequest,
        com.testsigma.web.request.TestDeviceResultRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateTestSuiteResultData(com.testsigma.automator.entity.TestSuiteResultRequest testSuiteResultRequest) throws AutomatorException {
    try {
      testSuiteResultService.updateResultData(convertToObject(testSuiteResultRequest,
        com.testsigma.web.request.TestSuiteResultRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateTestCaseResultData(TestCaseResultRequest testCaseResultRequest) throws AutomatorException {
    try {
      testCaseResultService.updateResultData(convertToObject(testCaseResultRequest,
        com.testsigma.web.request.TestCaseResultRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }


  @Override
  public TestCaseEntity getTestCase(Long environmentResultId, TestCaseEntity testCaseEntity) throws AutomatorException {
    try {
      TestCaseEntityDTO testCaseEntityDTO = testCaseService.find(testCaseEntity.getId(), environmentResultId,
        testCaseEntity.getTestDataSetName(), testCaseEntity.getTestCaseResultId());
      TestCaseEntity entity = convertToObject(testCaseEntityDTO, TestCaseEntity.class);
      log.info("Returning test case entity to test engine - " + entity);
      return entity;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }


  @Override
  public void updateElement(String name, ElementRequestEntity elementRequestEntity) throws AutomatorException {
    try {
      elementService.updateByName(name, convertToObject(elementRequestEntity, ElementRequest.class));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public String getRunTimeData(String variableName, Long environmentResultId, String sessionId) throws AutomatorException {
    try {
      return runTimeDataService.getRunTimeData(variableName, environmentResultId, sessionId);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public void updateRunTimeData(Long environmentResultId, RuntimeEntity runtimeEntity) throws AutomatorException {
    try {
      RuntimeRequest runtimeRequest = convertToObject(runtimeEntity, RuntimeRequest.class);
      runTimeDataService.updateRunTimeData(environmentResultId, runtimeRequest);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  @Override
  public WebDriverSettingsDTO getWebDriverSettings(Long environmentResultId) throws AutomatorException {
    try {
      WebDriverSettingsDTO webDriverSettingsDTO = convertToObject(webDriverSettingsService.getCapabilities(
        environmentResultId), WebDriverSettingsDTO.class);
      log.info("Responding back with web driver settings DTO - " + webDriverSettingsDTO);
      return webDriverSettingsDTO;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }


  @Override
  public String getDriverExecutablePath(String browserName, String browserVersion)
    throws AutomatorException {
    return "";
  }

  @Override
  public List<SuggestionEntity> getSuggestions(Integer naturalTextActionId) throws AutomatorException {
    try {
      List<SuggestionDTO> suggestionDTOS = suggestionMappingService.findAllByNaturalTextActionId(naturalTextActionId);
      return suggestionMapper.map(suggestionDTOS);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }
}
