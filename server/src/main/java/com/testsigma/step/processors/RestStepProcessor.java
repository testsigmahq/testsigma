package com.testsigma.step.processors;

import com.testsigma.model.StorageAccessLevel;
import com.testsigma.constants.MessageConstants;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.*;
import com.testsigma.exception.ExceptionErrorCodes;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.TestDataSet;
import com.testsigma.model.*;
import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.context.WebApplicationContext;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class RestStepProcessor extends StepProcessor {
  private final static String TESTSIGMA_STORAGE = "testsigma-storage:";

  public RestStepProcessor(WebApplicationContext webApplicationContext, List<TestCaseStepEntityDTO> testCaseStepEntityDTOS,
                           WorkspaceType workspaceType, Map<String, Element> elementMap,
                           TestStepDTO testStepDTO, Long testPlanId, TestDataSet testDataSet,
                           Map<String, String> environmentParams, TestCaseEntityDTO testCaseEntityDTO,
                           String environmentParamSetName, String dataProfile, Map<Long, Long> dataSetIndex) {
    super(webApplicationContext, testCaseStepEntityDTOS, workspaceType, elementMap, testStepDTO, testPlanId, testDataSet,
      environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile, dataSetIndex);
  }

  public void process() throws TestsigmaException {
    TestCaseStepEntityDTO testCaseStepEntityDTO = new TestCaseStepEntityDTO();
    testCaseStepEntityDTO.setId(testStepDTO.getId());
    processDefault(testCaseStepEntityDTO);
    testCaseStepEntityDTOS.add(testCaseStepEntityDTO);
    Map<String, Object> restDetails = new HashMap<>();
    try {
      if (testCaseEntityDTO.getTestDataId() != null) {
        TestData testData = testDataProfileService.find(testCaseEntityDTO.getTestDataId());
      }
      RestStep restStep = restStepService.findByStepId(testStepDTO.getId());
      //in restful application if the type is custom function skip
      if (restStep == null) {
        return;
      }
      RestStepDTO restEntity = testStepMapper.map(restStep);

      processMultipart(restEntity);

      if (testStepDTO.getConditionType() != TestStepConditionType.CONDITION_ELSE) {
        restEntity.setRequestHeaders(replaceTestDataAndEnvironmentParams(ObjectUtils.defaultIfNull(
          restEntity.getRequestHeaders(), new JSONObject())));
        restEntity.setAuthorizationValue(replaceTestDataAndEnvironmentParams(ObjectUtils.defaultIfNull(
          restEntity.getAuthorizationValue(), new JSONObject())));

        restEntity.setUrl(replaceTestDataAndEnvironmentParams(restEntity.getUrl()));
        restEntity.setPayload(replaceTestDataAndEnvironmentParams(restEntity.getPayload()));
        restEntity.setResponse(replaceTestDataAndEnvironmentParams(restEntity.getResponse()));
        restEntity.setStatus(replaceTestDataAndEnvironmentParams(restEntity.getStatus()));
        restDetails.put("rest_details", testStepMapper.mapStepEntity(restEntity));

        testCaseStepEntityDTO.setIfConditionExpectedResults(testStepDTO.getIfConditionExpectedResults());
        testCaseStepEntityDTO.setAdditionalData(testStepDTO.getDataMapJson());
      }

    } catch (TestsigmaException e) {
      log.error(e.getMessage(), e);
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e.getMessage());
    }
    testCaseStepEntityDTO.setStepGroupId(testStepDTO.getStepGroupId());
    testCaseStepEntityDTO.setParentId(testStepDTO.getParentId());
    testCaseStepEntityDTO.setConditionType(testStepDTO.getConditionType());

    testCaseStepEntityDTO.setTestCaseId(testStepDTO.getTestCaseId());

    if (testStepDTO.getDataMapJson() != null) {
      restDetails.putAll(testStepDTO.getDataMapJson());
    }
    testCaseStepEntityDTO.setAdditionalData(restDetails);
    setStepDetails(testCaseStepEntityDTO, testStepDTO);
  }

  public void setStepDetails(TestCaseStepEntityDTO testCaseStepEntityDTO, TestStepDTO testStepDTO) {
    StepDetailsDTO stepDetails = new StepDetailsDTO();
    stepDetails.setNaturalTextActionId(testStepDTO.getNaturalTextActionId());
    stepDetails.setAction(testStepDTO.getAction());
    stepDetails.setPriority(testStepDTO.getPriority());
    stepDetails.setPreRequisiteStepId(testStepDTO.getPreRequisiteStepId());
    stepDetails.setConditionType(testStepDTO.getConditionType());
    stepDetails.setParentId(testStepDTO.getParentId());
    stepDetails.setDataMap(testStepMapper.mapDataMap(testStepDTO.getDataMapBean()));
    stepDetails.setType(testStepDTO.getType());
    stepDetails.setStepGroupId(testStepDTO.getStepGroupId());
    stepDetails.setPosition(testStepDTO.getPosition());
    stepDetails.setTestDataName(testCaseStepEntityDTO.getTestDataName());
    stepDetails.setTestDataValue(testCaseStepEntityDTO.getTestDataValue());
    testCaseStepEntityDTO.setStepDetails(stepDetails);
  }

  private JSONObject replaceTestDataAndEnvironmentParams(JSONObject requestString)
    throws TestsigmaException {
    return new JSONObject(replaceTestDataAndEnvironmentParams(requestString.toString()));
  }

  private String replaceTestDataAndEnvironmentParams(String requestString)
    throws TestsigmaException {
    String testDataReplacedString = replaceTestDataParams(requestString, testDataSet,
      testCaseEntityDTO.getTestCaseName(), dataProfile);
    return replaceEnvironmentDataParams(testDataReplacedString, environmentParameters, environmentParamSetName,
      testCaseEntityDTO.getTestCaseName());
  }

  protected String replaceTestDataParams(String inputString, TestDataSet dataSet, String testCaseName,
                                         String testDataName)
    throws TestsigmaException {
    if (inputString == null) {
      return null;
    }

    int first = inputString.indexOf(NaturalTextActionConstants.REST_DATA_PARAM_START_PATTERN);
    int second = inputString.indexOf(NaturalTextActionConstants.REST_DATA_PARAM_END_PATTERN, first + 2);

    while (first >= 0 && second > 0) {
      String data = inputString.substring(first + 2, second);
      data = data.trim();

      if (dataSet == null) {
        String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
          MessageConstants.MSG_UNKNOWN_TEST_DATA_DATA, testCaseName);
        throw new TestsigmaException(ExceptionErrorCodes.TEST_DATA_SET_NOT_FOUND, errorMessage);
      }
      String parameter = ObjectUtils.defaultIfNull(dataSet.getData(), new JSONObject()).optString(data);

      if (StringUtils.isEmpty(parameter)) {
        String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
          MessageConstants.MSG_UNKNOWN_TEST_DATA_PARAMETER_NOTIN_TEST_STEP, data, testDataName);
        throw new TestsigmaException(ExceptionErrorCodes.TEST_DATA_NOT_FOUND, errorMessage);
      }
      inputString = inputString.replaceAll(NaturalTextActionConstants.REST_DATA_PARAM_START_ESCAPED_PATTERN
        + Pattern.quote(data) + NaturalTextActionConstants.REST_DATA_PARAM_END_ESCAPED_PATTERN, parameter);

      first = inputString.indexOf(NaturalTextActionConstants.REST_DATA_PARAM_START_PATTERN);
      second = inputString.indexOf(NaturalTextActionConstants.REST_DATA_PARAM_END_PATTERN, first + 2);

    }
    return inputString;
  }

  private String replaceEnvironmentDataParams(String inputString, Map<String, String> environmentDataSet, String environmentDataName,
                                              String testCaseName) throws TestsigmaException {

    if (inputString == null) {
      return null;
    }

    int first = inputString.indexOf(NaturalTextActionConstants.REST_DATA_ENVIRONMENT_PARAM_START_PATTERN);
    int second = inputString.indexOf(NaturalTextActionConstants.REST_DATA_ENVIRONMENT_PARAM_END_PATTERN, first + 2);

    while (first >= 0 && second > 0) {
      String data = inputString.substring(first + 2, second);
      data = data.trim();


      if (environmentDataSet == null) {
        String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
          MessageConstants.MSG_UNKNOWN_ENVIRONMENT_DATA_SET);
        throw new TestsigmaException(ExceptionErrorCodes.ENVIRONMENT_PARAMETERS_NOT_CONFIGURED, errorMessage);
      }

      String parameter = environmentDataSet.getOrDefault(data, "");
      if (StringUtils.isEmpty(parameter)) {
        String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
          MessageConstants.MSG_UNKNOWN_ENVIRONMENT_PARAMETER_IN_TEST_STEP, parameter, testCaseName, environmentDataName);
        throw new TestsigmaException(ExceptionErrorCodes.ENVIRONMENT_PARAMETER_NOT_FOUND, errorMessage);
      }

      inputString = inputString.replaceAll(NaturalTextActionConstants.REST_DATA_ENVIRONMENT_PARAM_START_ESCAPED_PATTERN + Pattern.quote(data)
        + NaturalTextActionConstants.REST_DATA_ENVIRONMENT_PARAM_END_ESCAPED_PATTERN, Matcher.quoteReplacement(parameter));

      first = inputString.indexOf(NaturalTextActionConstants.REST_DATA_ENVIRONMENT_PARAM_START_PATTERN);
      second = inputString.indexOf(NaturalTextActionConstants.REST_DATA_ENVIRONMENT_PARAM_END_PATTERN, first + 2);

    }
    return inputString;
  }

  private void processMultipart(RestStepDTO dto) throws Exception {
    String payload = dto.getPayload();
    Boolean isMultiPart;
    if (payload != null && isJSONValid(payload)) {
      if (isJSONArray(payload)) {
        List<Object> body = new ObjectMapperService().parseJson(payload, List.class);
        List<Object> dupbody = new ObjectMapperService().parseJson(payload, List.class);
        isMultiPart = updatePreSignedUrls(body, dupbody);
        payload = new ObjectMapperService().convertToJson(dupbody);
      } else {
        Map<String, Object> body = new ObjectMapperService().parseJson(payload, Map.class);
        Map<String, Object> dupbody = new ObjectMapperService().parseJson(payload, Map.class);
        isMultiPart = updatePreSignedUrls(body, dupbody);
        payload = new ObjectMapperService().convertToJson(dupbody);
      }
      dto.setIsMultipart(isMultiPart);
      dto.setPayload(payload);
    }
  }

  public boolean isJSONValid(String jsonStr) {
    try {
      new JSONObject(jsonStr);
    } catch (JSONException ex) {
      try {
        new JSONArray(jsonStr);
      } catch (JSONException ex1) {
        return false;
      }
    }
    return true;
  }

  public boolean isJSONArray(String jsonStr) {
    try {
      new JSONArray(jsonStr);
    } catch (Exception ex1) {
      return false;
    }
    return true;
  }

  private Boolean updatePreSignedUrls(Object org, Object nested) {
    Boolean hasFile = false;
    if (org instanceof List) {
      List orgList = (List) org;
      List nestedList = (List) nested;
      for (int i = 0; i < orgList.size(); i++) {
        hasFile = (!hasFile) ? updatePreSignedUrls(orgList.get(i), nestedList.get(i)) : hasFile;
      }
    } else if (org instanceof Map) {
      Map<String, Object> nestedBody = (Map) org;
      Map<String, Object> dupBody = (Map) nested;
      for (Map.Entry<String, Object> entry : nestedBody.entrySet()) {
        if (entry.getValue() != null && entry.getValue() instanceof String) {
          String sigUrl = getFilePreSignedUrl(entry.getValue().toString());
          if (sigUrl != null) {
            dupBody.put(entry.getKey(), sigUrl);
            hasFile = true;
          }
        } else if (entry.getValue() instanceof Object) {
          hasFile = (!hasFile) ? updatePreSignedUrls(entry.getValue(), dupBody.get(entry.getKey())) : hasFile;
        }
      }
    }
    return hasFile;
  }

  private String getFilePreSignedUrl(String fileUrl) {
    if (!org.apache.commons.lang3.StringUtils.isEmpty(fileUrl) && fileUrl.startsWith(TESTSIGMA_STORAGE)) {
      fileUrl = fileUrl.replace(TESTSIGMA_STORAGE, "tenants");
      Optional<URL> newPreSignedURL = storageService.generatePreSignedURLIfExists(
        fileUrl.replace("tenants" + "/", ""),
        StorageAccessLevel.READ, 300
      );
      if (newPreSignedURL != null && newPreSignedURL.isPresent()) {
        fileUrl = newPreSignedURL.get().toString();
      }
      return fileUrl;
    }
    return null;
  }
}
