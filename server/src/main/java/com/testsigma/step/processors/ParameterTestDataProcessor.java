package com.testsigma.step.processors;

import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.dto.TestCaseStepEntityDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.model.TestStep;
import com.testsigma.model.TestStepDataMap;
import com.testsigma.service.TestDataSetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

@Log4j2
public class ParameterTestDataProcessor extends TestDataProcessor{
  protected com.testsigma.model.TestDataSet testDataSet;
  protected Integer index;
  protected  Long stepId;
  protected TestData testData;
  protected Map<Long, Long> stepGroupParentForLoopStepIdTestDataSetMap;
  public static final Long OVERRIDE_STEP_GROUP_STEP_WITH_TEST_CASE_PROFILE_ID = -2l;
  protected TestDataSetService testDataSetService;
  String TEST_DATA_NOT_FOUND = "Test Step is not Executed Because TestData parameter is not found %s with in selected step id Test data profile.";
  private final String TEST_DATA_OUT_OF_RANGE = "selected test data profile %s size %s is less than in index %s";
  private final String TEST_DATA_UNKNOWN_ERROR = "Unknown error occurred while processing test data profile %s with index %s and name %s";
  private final String STEP_GROUP_ERROR_MESSAGE = "The TestData parameter is overridden with the StepGroup TestData parameter But TestData profile is not selected";
  private final String TEST_CASE_ERROR_MESSAGE = "The TestData parameter is overridden with the TestCase TestData parameter But TestData profile is not selected";
  private final String PARENT_STEP_ERROR_MESSAGE = "The TestData parameter is overridden with the parent data parameter profile but it is not available";
  private final String STEP_GROUP_OVERRIDDEN_STALE_ERROR_MESSAGE = "The TestData parameter is overridden.  but it is not available";
  private final String PARENT_STEP_NOT_FOUND_ERROR_MESSAGE = "The TestData parameter is overridden.  but it is not available";

  public ParameterTestDataProcessor(TestCaseEntityDTO testCaseEntityDTO,
                                    TestCaseStepEntityDTO testCaseStepEntityDTO,
                                    Map<Long, Long> stepGroupParentForLoopStepIdTestDataSetMap,
                                    com.testsigma.model.TestDataSet testDataSet, String parameter,
                                    TestDataPropertiesEntity testDataPropertiesEntity,
                                    WebApplicationContext context) {
    super(testCaseStepEntityDTO, testCaseEntityDTO, testDataPropertiesEntity, context);
    this.testCaseEntityDTO = testCaseEntityDTO;
    this.testCaseStepEntityDTO = testCaseStepEntityDTO;
    this.stepGroupParentForLoopStepIdTestDataSetMap = stepGroupParentForLoopStepIdTestDataSetMap;
    this.testDataSet = testDataSet;
    this.parameter = parameter;
    this.testDataSetService =  (TestDataSetService) context.getBean("testDataSetService");
  }

  public void processTestData() {
    if(!isValueSet){
      Long testDataProfileStepId = testCaseStepEntityDTO.getTestDataProfileStepId();
      Boolean isTestCaseTestDataProfileSelected =  testDataProfileStepId!= null && testDataProfileStepId == -1;
      Boolean isParentForLoopSelected =  testDataProfileStepId!= null && testDataProfileStepId > 0;
      if(isTestCaseTestDataProfileSelected){
        processTestCaseParameter(testCaseEntityDTO.getId(), testCaseEntityDTO.getTestDataIndex());
      } else if(isParentForLoopSelected){
        stepId = testDataProfileStepId;
        processOverRiddenParentStepParameter();
      } else {
        processTestData(testDataSet, parameter);
      }
    }
    if(!this.isValueSet){
      setDefaultMessage();
    }
  }


  private void processOverRiddenParentStepParameter(){
    try {
      TestStep testStep = testStepService.find(stepId);
      TestData testData = testDataService.find(testStep.getForLoopTestDataId());
      processLoopParameter(testData, parameter,
              this.stepGroupParentForLoopStepIdIndexes.get(stepId));
    }catch (ResourceNotFoundException exception){
      this.exception = exception;
      log.error(exception, exception);
      setParentStepErrorMessage();
    }
  }

  public void processTestCaseParameter(Long testCaseId, Integer index) {
    try {
      TestData testData = testDataService.find(testCaseService.find(testCaseId).getTestDataId());
      processLoopParameter(testData, parameter, testData.getTempTestData().get(index));
    }catch (ResourceNotFoundException exception){
      this.exception = exception;
      log.error(exception, exception);
      setTestCaseErrorMessage();
    }
  }

  public void processLoopParameter(TestData testData, String parameter, TestDataSet testDataSet) {
    try {
      this.index = index;
      this.parameter = parameter;
      this.testData = testData;
      processTestData(testDataSet, parameter);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      exception = indexOutOfBoundsException;
      setForLoopErrorMessage();
    } catch (Exception e) {
      exception = e;
      log.error(exception, exception);
      setForLoopErrorMessage();
    }
  }

  protected void processTestData(TestDataSet testDataSet, String parameter) {
    try {
      this.parameter = parameter;
      value = testDataSet.getData().getString(parameter);
      this.isValueSet = true;
    } catch (JSONException jsonException) {
      log.error(jsonException, jsonException);
      setErrorMessage();
    }
  }

  protected void setErrorMessage() {
   super.setErrorMessage();
   testCaseStepEntityDTO.setFailureMessage(String.format(TEST_DATA_NOT_FOUND, parameter));
  }

  protected void setForLoopErrorMessage() {
    super.setErrorMessage();
    if (exception instanceof IndexOutOfBoundsException)
      testCaseStepEntityDTO.setFailureMessage(String.format(TEST_DATA_OUT_OF_RANGE,
        testData.getTestDataName(), testData.getTempTestData().size(), index));
    else {
      testCaseStepEntityDTO.setFailureMessage(String.format(TEST_DATA_UNKNOWN_ERROR,
        testData.getTestDataName(), index, parameter));
    }
  }

  protected void setParentStepErrorMessage() {
    super.setErrorMessage();
    testCaseStepEntityDTO.setFailureMessage(PARENT_STEP_NOT_FOUND_ERROR_MESSAGE);
  }

  protected void setStepGroupErrorMessage() {
    super.setErrorMessage();
    testCaseStepEntityDTO.setFailureMessage(STEP_GROUP_OVERRIDDEN_STALE_ERROR_MESSAGE);
  }

  protected void setTestCaseErrorMessage() {
    super.setErrorMessage();

    if(stepId == -1 && testCaseEntityDTO.getIsStepGroup())
      testCaseStepEntityDTO.setFailureMessage(STEP_GROUP_ERROR_MESSAGE);
    else if(stepId == -1 && testCaseEntityDTO.getIsStepGroup())
      testCaseStepEntityDTO.setFailureMessage(TEST_CASE_ERROR_MESSAGE);
    else
      testCaseStepEntityDTO.setFailureMessage(PARENT_STEP_ERROR_MESSAGE);
  }

  protected void setDefaultMessage() {
    super.setErrorMessage();
    testCaseStepEntityDTO.setFailureMessage(String.format(TEST_DATA_NOT_FOUND, parameter));
  }

}
