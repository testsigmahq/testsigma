package com.testsigma.automator.suggestion;

import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.constants.ActionResult;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.FindByType;
import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import com.testsigma.automator.suggestion.entity.SuggestionEntity;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

@Log4j2
public class SuggestionRunner {
  private final TestCaseStepEntity testCaseStepEntity;
  private final TestCaseStepResult testCaseStepResult;
  protected TestPlanRunSettingEntity settings;
  private Map<String, String> envSettings = new HashMap<String, String>();

  public SuggestionRunner(TestCaseStepEntity testCaseStepEntity,
                          TestCaseStepResult testCaseStepResult,
                          TestPlanRunSettingEntity settings,
                          Map<String, String> envSettings) {

    this.testCaseStepEntity = testCaseStepEntity;
    this.testCaseStepResult = testCaseStepResult;
    this.settings = settings;
    this.envSettings = envSettings;

  }

  public void diagniseStep() {
    List<SuggestionEntity> possibleFixesList;
    try {
      possibleFixesList = ObjectUtils.defaultIfNull(AutomatorConfig.getInstance().getAppBridge().
        getSuggestions(testCaseStepEntity.getNaturalTextActionId()), new ArrayList<>());
    } catch (AutomatorException e) {
      log.error("Unable to fetch suggestions from suggestions AI", e);
      return;
    }
    diagniseStep(possibleFixesList);
  }

  public void diagniseStep(List<SuggestionEntity> possibleFixesList) {
    SuggestionEngineResult result = null;
    for (SuggestionEntity entity : possibleFixesList) {
      try {
        result = new SuggestionEngineResult();
        result.setMetaData(new SuggestionEngineResultMetaData());
        SuggestionAction snippet = prepareSnippet(entity.getSnippetClass(), settings, envSettings);
        snippet.engineResult = result;
        SuggestionAction.setTestCaseStepEntity(testCaseStepEntity);
        result = tryFix(snippet, entity.getId());
        result.setMessage(entity.getDisplayName());
        result.setMetaData(snippet.engineResult.getMetaData());
        testCaseStepResult.getSuggestionResults().add(result);
      } catch (Exception e) {
        log.error(e, e);
        result.setMessage(entity.getDisplayName());
        result.setResult(SuggestionActionResult.Failure);
        testCaseStepResult.getSuggestionResults().add(result);
      }
      result.setSuggestionId(entity.getId());
    }
  }

  protected SuggestionEngineResult tryFix(SuggestionAction snippet, Integer fix) {
    SuggestionEngineResult res = new SuggestionEngineResult();
    res.setMetaData(new SuggestionEngineResultMetaData());
    ActionResult snippetResult = null;
    try {
      snippetResult = snippet.run();
      if (snippetResult == ActionResult.SUCCESS) {
        res.setResult(res.getResult() == SuggestionActionResult.Failure ?
          SuggestionActionResult.Failure : SuggestionActionResult.Success);
        res.setSuggestionId(fix);
        return res;
      } else {
        res.setResult(SuggestionActionResult.Failure);
      }
    } catch (Exception e) {
      log.error(e, e);
      res.setResult(SuggestionActionResult.Failure);
    }
    return res;
  }

  protected SuggestionAction prepareSnippet(String snippetClass, TestPlanRunSettingEntity executionSettings,
                                             Map<String, String> envSetting) throws ClassNotFoundException,
    IllegalAccessException, InstantiationException, AutomatorException {

    Class className = Class.forName(snippetClass);
    SuggestionAction snippet = (SuggestionAction) className.newInstance();
    //convertTestStepDataToNewFormat(testCaseStepEntity);
    snippet.setDriver(DriverManager.getRemoteWebDriver());
    //  snippet.setTimeout(testCaseStepEntity.getWaitTime().longValue());
    snippet.setTimeout(0l);
    SuggestionAction.setTestCaseStepEntity(testCaseStepEntity);
    snippet.setTestDataPropertiesEntityMap(testCaseStepEntity.getTestDataMap());
    snippet.setElementPropertiesEntityMap(testCaseStepEntity.getElementsMap());
    snippet.setAttributesMap(testCaseStepEntity.getAttributesMap());
    snippet.setGlobalElementTimeOut(executionSettings.getElementTimeOut().longValue());
    snippet.setRuntimeDataProvider(prepareRunTimeDataProvider());
    snippet.setEnvSettings(envSetting);
    snippet.setAdditionalData(testCaseStepEntity.getAdditionalData());
    return snippet;
  }

  private void convertTestStepDataToNewFormat(TestCaseStepEntity testStepEntity) {

    LinkedHashMap<String, TestDataPropertiesEntity> testdatasMap = new LinkedHashMap<>();
    Map<String, ElementPropertiesEntity> elementsMap = new HashMap<>();
    Map<String, AttributePropertiesEntity> attrubutesMap = new HashMap<>();

    if (testStepEntity.getTestDataValue() != null) {
      TestDataPropertiesEntity testDataPropertiesEntity = new TestDataPropertiesEntity();
      testDataPropertiesEntity.setTestDataValue(testStepEntity.getTestDataValue());
      testDataPropertiesEntity.setTestDataName(testStepEntity.getTestDataName());
      testDataPropertiesEntity.setTestDataType(testStepEntity.getTestDataType());
      testDataPropertiesEntity.setTestDataValuePreSignedURL(testStepEntity.getTestDataValuePreSignedURL());
      if (testStepEntity.getTestDataType().equals(TestDataType.function.name())) {
        testDataPropertiesEntity.setTestDataFunction((Map<String, Object>) testStepEntity.getAdditionalData().get(NaturalTextActionConstants.KEY_CUSTOM_TEST_DATA_FUN));
      }
      //testDataPropertiesEntity.setActionVariableName(ActionConstants.TESTSTEP_DATAMAP_KEY_TEST_DATA);
      testdatasMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, testDataPropertiesEntity);
    }
    if (testStepEntity.getElementName() != null) {
      ElementPropertiesEntity elementPropertiesEntity = new ElementPropertiesEntity();
      elementPropertiesEntity.setLocatorValue(testStepEntity.getLocatorValue());
      elementPropertiesEntity.setElementName(testStepEntity.getElementName());
      LocatorType locatorType = LocatorType.valueOf(testStepEntity.getLocatorStrategy());
      elementPropertiesEntity.setFindByType(FindByType.getType(locatorType));
      elementPropertiesEntity.setActionVariableName(NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT);
      elementsMap.put(NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT, elementPropertiesEntity);

    }
    if (testStepEntity.getAttribute() != null) {
      AttributePropertiesEntity attributePropertiesEntity = new AttributePropertiesEntity();
      attributePropertiesEntity.setAttributeName(testStepEntity.getAttribute());
      attributePropertiesEntity.setActionVariableName(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_ATTRIBUTE);
      attrubutesMap.put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_ATTRIBUTE, attributePropertiesEntity);
    }
    testStepEntity.setElementsMap(elementsMap);
    testStepEntity.setTestDataMap(testdatasMap);
    testStepEntity.setAttributesMap(attrubutesMap);
  }

  private RuntimeDataProvider prepareRunTimeDataProvider() {
    return new RuntimeDataProvider();
  }
}
