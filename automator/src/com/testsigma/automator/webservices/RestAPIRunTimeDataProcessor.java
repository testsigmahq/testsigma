package com.testsigma.automator.webservices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.entity.RestfulStepEntity;
import com.testsigma.automator.entity.TestCaseStepResult;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.service.ObjectMapperService;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Data
public class RestAPIRunTimeDataProcessor {
  private RestfulStepEntity restfulStepEntity;
  private TestCaseStepResult testCaseStepResult;
  private static final String JSON_PATH_ALL = "*";
  private static final String MSG_REST_RESPONSE_JSON_PATH_NOT_EXIST = "Error while saving response data to Runtime variables." +
    "Following Json Paths does not exist in response<br>" +
    "Json Paths that are not present in response:<b>%s</b>. <br>Please validate the JSON Path's against response and update them.";
  private static final String MSG_RUN_TIME_VARIABLE_NOT_SET = "There is no runtime value available for property: <b>%s</b>." +
    "<br>Please set the runtime property before reaching this step.";
  public RestAPIRunTimeDataProcessor(RestfulStepEntity restfulStepEntity, TestCaseStepResult testCaseStepResult) {
    this.restfulStepEntity = restfulStepEntity;
    this.testCaseStepResult = testCaseStepResult;
    }

  public void processRestAPIStep() throws AutomatorException {
    replaceAPIRequestRuntimeData();
    replaceExpectedResponseRuntimeData();
  }

  private void replaceExpectedResponseRuntimeData() throws AutomatorException {
    updateExpectedBodyRuntimeVariables();
    updateExpectedHeaderRuntimeVariables();
    updateExpectedStatusRuntimeVariables();
  }

  private void replaceAPIRequestRuntimeData() throws AutomatorException {
    updateRequestUrlRuntimeValues();
    updateRequestHeadersRuntimeValues();
    updateRequestAuthorizationRuntimeValues();
    updateRequestBodyRuntimeValues();
  }

  private String replaceRuntimeVariables(String inputData) throws AutomatorException {
    log.debug("Replacing runtime parameters in :" + inputData);
    String updatedData = inputData;
    String[] names = StringUtils.substringsBetween(inputData, NaturalTextActionConstants.restDataiRunStartPattern, NaturalTextActionConstants.restDataiRunaEndPattern);
    if (names == null) {
      return inputData;
    }
    for (String runTimeVarName : names) {
      String runTimeValue = new RuntimeDataProvider().getRuntimeData(runTimeVarName);
      if (runTimeValue == null) {
        throw new AutomatorException(String.format(MSG_RUN_TIME_VARIABLE_NOT_SET, runTimeVarName));
      }
      String runTimeVarPattern = String.format("%s%s%s", NaturalTextActionConstants.restDataRunStartPattern, Pattern.quote(runTimeVarName), NaturalTextActionConstants.restDatRunaEndPattern);
      updatedData = updatedData.replaceAll(runTimeVarPattern, Matcher.quoteReplacement(runTimeValue));
      log.debug(String.format("Replaced runtime var:%s with %s", runTimeVarName, runTimeValue));
    }
    return updatedData;

  }

  private void updateRequestUrlRuntimeValues() throws AutomatorException {
    log.debug("Updating runtime variables in URL, RestStep ID:" + restfulStepEntity.getId());
    try{
      String updatedURL = replaceRuntimeVariables(restfulStepEntity.getUrl());
      restfulStepEntity.setUrl(updatedURL.trim());
    }catch(AutomatorException e){
      throw new AutomatorException("Error while replacing runtime variables in request URL."+e.getMessage());
    }

  }

  private void updateRequestHeadersRuntimeValues() throws AutomatorException {
    log.debug("Updating header run time data for Rest Step:" + restfulStepEntity.getId());
    try{
    String updatedRequestHeaders = replaceRuntimeVariables(restfulStepEntity.getRequestHeaders());
    restfulStepEntity.setRequestHeaders(updatedRequestHeaders);
  }catch(AutomatorException e){
    throw new AutomatorException("Error while replacing runtime variables in request Headers."+e.getMessage());
  }
  }

  private void updateExpectedBodyRuntimeVariables() throws AutomatorException {
    log.debug("Updating runtime data in expected body,RestStep ID:" + restfulStepEntity.getId());
    try{
    String updatedExpectedResponse = replaceRuntimeVariables(restfulStepEntity.getResponse());
    restfulStepEntity.setResponse(updatedExpectedResponse);
  }catch(AutomatorException e){
    throw new AutomatorException("Error while replacing runtime variables in Verify Response body."+e.getMessage());
  }
  }

  private void updateExpectedHeaderRuntimeVariables() throws AutomatorException {
    log.debug("Updating runtime data in expected Header, RestStep ID:" + restfulStepEntity.getId());
    try{
    String updatedExpectedHeaders = replaceRuntimeVariables(restfulStepEntity.getResponseHeaders());
    restfulStepEntity.setResponseHeaders(updatedExpectedHeaders);
    }catch(AutomatorException e){
      throw new AutomatorException("Error while replacing runtime variables in Verify Response headers."+e.getMessage());
    }
  }

  private void updateRequestBodyRuntimeValues() throws AutomatorException {
    log.debug("Updating Body runtime data for rest step:" + restfulStepEntity.getId());
    try{
    String updatedPayload = replaceRuntimeVariables(restfulStepEntity.getPayload());
    restfulStepEntity.setPayload(updatedPayload);
    }catch(AutomatorException e){
      throw new AutomatorException("Error while replacing runtime variables in request body."+e.getMessage());
    }
  }

  private void updateExpectedStatusRuntimeVariables() throws AutomatorException {
    log.debug("Updating Run time data in Status, RestStep ID:" + restfulStepEntity.getId());
    try{
    String updatedExpectedStatus = replaceRuntimeVariables(restfulStepEntity.getStatus());
    restfulStepEntity.setStatus(updatedExpectedStatus);
  }catch(AutomatorException e){
    throw new AutomatorException("Error while replacing runtime variables in Verify Response Status."+e.getMessage());
  }
  }

  private void updateRequestAuthorizationRuntimeValues() throws AutomatorException {
    log.debug("Updating Run time Data in Authorization data, RestStep ID:" + restfulStepEntity.getId());
   try{
    String updatedAuthData = replaceRuntimeVariables(restfulStepEntity.getAuthorizationValue());
    restfulStepEntity.setAuthorizationValue(updatedAuthData);
  }catch(AutomatorException e){
    throw new AutomatorException("Error while replacing runtime variables in Request's Authorization field."+e.getMessage());
  }
  }

  public void storeResponseData(HttpResponse<String> response,Map<String,String> envSettings) throws AutomatorException {
    String responseStr = response.getResponseText();
    Map<String, String> responseHeadersMap = response.getHeadersMap();
    String responseHeaders = new ObjectMapperService().convertToJson(responseHeadersMap);
    storeRuntimeVariblesJsonPath(restfulStepEntity.getHeaderRuntimeData(), responseHeaders,envSettings);
    storeRuntimeVariblesJsonPath(restfulStepEntity.getBodyRuntimeData(), responseStr,envSettings);
  }

  private void storeRuntimeVariblesJsonPath(String expectedStr, String actualStr,Map<String,String> envSettings) throws AutomatorException{

    if (StringUtils.isNotBlank(expectedStr) && StringUtils.isNotBlank(actualStr)) {
      boolean jsonPathValidationsFailed = false;
      List<String> failedJsonPathsList = new ArrayList<>();
      Map<String,String> runtimeVariablesMap = new HashMap<>();
      boolean isPathNotFound = false;
      String exceptionStr = "";
      Map<String, String> pathMap = new ObjectMapperService().parseJson(expectedStr, new TypeReference<>() {
      });
      for (Map.Entry<String, String> map : pathMap.entrySet()) {
        String name = map.getKey();
        String path = map.getValue();
        try {
          Object pathResult;
          if (path.equals(JSON_PATH_ALL)) {
            pathResult = actualStr;
          } else {
            pathResult = JsonPath.parse(actualStr).read(path);
          }
          new RuntimeDataProvider().storeRuntimeVariable(name, pathResult.toString());
          runtimeVariablesMap.put(name,String.valueOf(pathResult));
        } catch (PathNotFoundException e) {
          jsonPathValidationsFailed = true;
          failedJsonPathsList.add(path);
          exceptionStr = exceptionStr+", <br>"+name+"="+path;
          log.error("JSON Path Error while saving response to runtime variable.", e);
        }
      }
      if(jsonPathValidationsFailed){
        throw new AutomatorException(String.format(MSG_REST_RESPONSE_JSON_PATH_NOT_EXIST,failedJsonPathsList));
      }
      if(!StringUtils.isEmpty(exceptionStr)){
        throw new AutomatorException("Not able to find the json paths::"+exceptionStr);
      }
    }

  }

}
