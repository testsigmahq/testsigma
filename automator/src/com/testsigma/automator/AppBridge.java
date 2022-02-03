package com.testsigma.automator;

import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.suggestion.entity.SuggestionEntity;

import java.util.List;

public interface AppBridge {
  void postEnvironmentResult(EnvironmentRunResult environmentResult) throws AutomatorException;

  void postTestSuiteResult(TestSuiteResult testSuiteResult) throws AutomatorException;

  void postTestCaseResult(TestCaseResult testCaseResult) throws AutomatorException;

  void updateEnvironmentResultData(TestDeviceResultRequest testDeviceResultRequest) throws AutomatorException;

  void updateTestSuiteResultData(TestSuiteResultRequest testSuiteResultRequest) throws AutomatorException;

  void updateTestCaseResultData(TestCaseResultRequest testCaseResultRequest) throws AutomatorException;

  TestCaseEntity getTestCase(Long environmentResultId, TestCaseEntity testCaseEntity) throws AutomatorException;

  void updateElement(String name, ElementRequestEntity elementRequestEntity) throws AutomatorException;

  String getRunTimeData(String variableName, Long environmentResultId, String sessionId) throws AutomatorException;

  void updateRunTimeData(Long environmentResultId, RuntimeEntity runtimeEntity) throws AutomatorException;

  WebDriverSettingsDTO getWebDriverSettings(Long environmentResultId) throws AutomatorException;

  String getDriverExecutablePath(String browserName, String browserVersion)
    throws AutomatorException;

  List<SuggestionEntity> getSuggestions(Integer naturalTextActionId) throws AutomatorException;
}
