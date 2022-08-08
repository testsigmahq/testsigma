package com.testsigma.automator.webservices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.entity.RestfulStepEntity;
import com.testsigma.automator.entity.ResultConstant;
import com.testsigma.automator.entity.TestCaseStepResult;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;
import java.util.Map;

@Log4j2
public class RestApiResponseValidator {
  private static final String MSG_INVALID_JSON ="Response is not in valid JSON format.";
  private RestfulStepEntity restfulStepEntity;
  private HttpResponse<String> restApiResponse;
  private TestCaseStepResult testCaseStepResult;
  private static final String MSG_REST_RESPONSE_JSON_PATH_NOT_EXIST = "Error while validating/verifying response data." +
    "Following Json Path does not exist in response<br>" +
    "Json Paths that are not present in response:<b>%s</b>, <br>Please validate the JSON Path's against response and update it.";
  private static final String MSG_STATUS_MISMATCH = "Response status validation failed.<br>Actual Status from response:<b>%s</b>" +
    "<br>Expected status:<b>%s</b>";

  public RestApiResponseValidator(RestfulStepEntity restfulStepEntity, TestCaseStepResult testCaseStepResult,HttpResponse<String> restApiResponse) {
    this.restfulStepEntity = restfulStepEntity;
    this.testCaseStepResult = testCaseStepResult;
    this.restApiResponse = restApiResponse;
  }


  public void validateResponse() throws AutomatorException {
    List<Integer> compareTypes = restfulStepEntity.getResultCompareTypes();
    Integer responseStatus = restApiResponse.getStatusCode();
    String responseStr = restApiResponse.getResponseText();

    Map<String, String> responseHeadersMap = restApiResponse.getHeadersMap();
    String responseHeaders = new ObjectMapperService().convertToJson(responseHeadersMap);
    validateStatus(restfulStepEntity, responseStatus);
    validateHeaders(restfulStepEntity.getResponseHeaders(),responseHeaders);
    validateResponseBody(restfulStepEntity.getResponse(), responseStr, restfulStepEntity.getResponseCompareType(),
      AutomatorMessages.MSG_REST_ERROR_CONTENT);
  }
  private void validateStatus(RestfulStepEntity entity, Integer responseStatus) throws AutomatorException{
    Integer expectedStatus = Integer.parseInt(entity.getStatus());
    if (!(expectedStatus == null || expectedStatus.equals(-1)) && !expectedStatus.equals(responseStatus)) {
      throw new AutomatorException(String.format(MSG_STATUS_MISMATCH,responseStatus,expectedStatus));
    }
  }

  private void validateResponseBody(String expectedStr, String actualStr, String compareType, String msg) throws AutomatorException {
    if (StringUtils.isBlank(actualStr)) {
      return;
    }

    if (!isJSONValid(actualStr)) {
      throw new AutomatorException(MSG_INVALID_JSON);
    }

    switch (JSONCompareMode.valueOf(compareType)) {
      case STRICT:
      case LENIENT:
      case NON_EXTENSIBLE:
      case STRICT_ORDER:
        validateJson(expectedStr, actualStr, compareType, msg);
        break;
      case JSON_PATH:
        validateJsonPath(expectedStr, actualStr, msg);
        break;
      case JSON_SCHEMA:
        validateJsonSchema(expectedStr, actualStr, msg);
        break;
      default:
        break;
    }
  }

  private void validateJsonSchema(String expectedSchema, String responseStr, String msg) {
    if (StringUtils.isNotBlank(expectedSchema)) {
      try {
        JSONObject rawSchema = new JSONObject(new JSONTokener(expectedSchema));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(new JSONObject(responseStr));
      } catch (Exception e) {
        log.error("JSON Schema validation failed.", e);
        testCaseStepResult.setResult(ResultConstant.FAILURE);
        testCaseStepResult.setMessage(msg + AutomatorMessages.MSG_SEPARATOR + e.getMessage());
      }
    }
  }

  private void validateJsonPath(String expectedStr, String actualStr, String msg) throws AutomatorException {
    if (StringUtils.isNotBlank(expectedStr)) {
      Map<String, String> expectedMap = new ObjectMapperService().parseJson(expectedStr, new TypeReference<>() {
      });

      for (Map.Entry<String, String> map : expectedMap.entrySet()) {
        String path = map.getKey();
        String value = map.getValue();

        Object pathResult;
        try{
          pathResult = JsonPath.parse(actualStr).read(path);
        } catch (PathNotFoundException e) {
          log.error("JSON Path Error while validating response .", e);
          throw new AutomatorException(String.format(MSG_REST_RESPONSE_JSON_PATH_NOT_EXIST,path));
        }
        String pathResultStr;

        StringBuilder sb = (testCaseStepResult.getMessage() != null) ?
          new StringBuilder(testCaseStepResult.getMessage()).append(AutomatorMessages.MSG_SEPARATOR) : new StringBuilder();

        if (pathResult instanceof String || pathResult instanceof Number) {
          pathResultStr = pathResult.toString();
          if (!value.equals(pathResultStr)) {
            msg = sb.append(msg).append(AutomatorMessages.MSG_SEPARATOR)
              .append(AutomatorMessages.getMessage(AutomatorMessages.MSG_REST_ERROR_PATH, path)).toString();
            testCaseStepResult.setResult(ResultConstant.FAILURE);
            testCaseStepResult.setMessage(msg);
          }
        } else {
          pathResultStr = new ObjectMapperService().convertToJson(pathResult);
          if (!value.equals(pathResultStr)) {
            new ObjectMapperService().parseJson(pathResultStr, Object.class);
            validateJson(pathResultStr, value, JSONCompareMode.STRICT.name(), msg);
          }

        }
      }
    }
  }
  private void validateHeaders(String expectedHeaders,String actualHeaders) throws AutomatorException{
    if (StringUtils.isNotBlank(expectedHeaders)) {
      try{
        JSONAssert.assertEquals(expectedHeaders, actualHeaders, org.skyscreamer.jsonassert.JSONCompareMode.LENIENT);
      }catch(AssertionError assertionError){
        throw new AutomatorException("Response header(s) verification/validation failed. " +
          "Please verify if the response headers contains expected headers."+assertionError.getMessage());
      }
    }
  }
  private void validateJson(String expectedStr, String actualStr, String compareType, String msg) {
    if (StringUtils.isNotBlank(expectedStr)) {
      try {
        JSONAssert.assertEquals(expectedStr, actualStr, org.skyscreamer.jsonassert.JSONCompareMode.valueOf(compareType));
      } catch (AssertionError ex) {
        testCaseStepResult.setResult(ResultConstant.FAILURE);

        StringBuilder sb = (testCaseStepResult.getMessage() != null) ?
          new StringBuilder(testCaseStepResult.getMessage()).append(AutomatorMessages.MSG_SEPARATOR) : new StringBuilder();
        testCaseStepResult.setMessage(sb.append(msg).append(AutomatorMessages.MSG_SEPARATOR).append(ex.getMessage()).toString());
        log.error(ex, ex);
      } catch (Exception ex) {

        testCaseStepResult.setResult(ResultConstant.FAILURE);
        StringBuilder sb = (testCaseStepResult.getMessage() != null) ?
          new StringBuilder(testCaseStepResult.getMessage()).append(AutomatorMessages.MSG_SEPARATOR) : new StringBuilder();

        testCaseStepResult.setMessage(sb.append(msg).append(AutomatorMessages.MSG_SEPARATOR).append(ex.getMessage()).toString());
        log.error(ex, ex);

      }
    }
  }
  private boolean isJSONValid(String jsonStr) {
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
}
