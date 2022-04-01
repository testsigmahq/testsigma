package com.testsigma.automator.constants;

public class AutomatorMessages {

  public final static String MSG_SEPARATOR = ";";
  public final static String MSG_USER_ABORTED_EXECUTION = "User stopped the Test Plan";
  public final static String MSG_ENVIRONMENT_SUCCESS = "Test Environment Test Plan completed";
  public final static String MSG_STEP_PRE_REQUISITE_FAILED = "Test Step pre-requisite failed";
  public final static String MSG_STEP_MAJOR_STEP_FAILURE = "Major Test step failed .";
  public final static String MSG_TEST_CASE_ABORTED = "Test Case execution Aborted on session failure.";
  public final static String MSG_STEP_PARENT_FAILED = "Unable to execute step because parent step has failed";
  public static final String MSG_STEP_PARENT_CONDITION_FAILED = "This step is part of a conditional IF, ELSE IF or ELSE statement and could not be executed since the primary condition required for it to execute was FALSE";
  public final static String MSG_CHECK_FOR_MORE_DETAILS = "Check step results for more details.";
  public final static String MSG_STEP_SUCCESS = "Test Step executed successfully";
  public final static String MSG_STEP_FAILURE = "Test Step execution failed";
  public final static String MSG_STEP_GROUP_SUCCESS = "Step Group executed successfully";
  public final static String MSG_STEP_GROUP_FAILURE = "Step Group execution failed";
  public final static String MSG_TEST_CASE_SUCCESS = "Test Case executed successfully";
  public final static String MSG_TEST_CASE_FAILURE = "Test Case execution failed. Check step results for more details";
  public final static String MSG_CASE_PRE_REQUISITE_FAILED = "Test Case pre-requisite failed";
  public final static String MSG_GROUP_SUCCESS = " Test Suite executed successfully";
  public final static String MSG_GROUP_FAILED = " Test Suite execution failed";
  public final static String MSG_GROUP_PRE_REQUISITE_FAILED = "Test Suite pre-requisite failed";
  public final static String MSG_ITERATION_SUCCESS = "Iteration {?1} executed successfully";
  public final static String MSG_ITERATION_FAILURE = "Iteration {?1} execution failed";
  public final static String MSG_CONDITION_IF_SUCCESS = "\"If\" condition matched to proceed with the steps under this condition.";
  public final static String MSG_CONDITION_ELSE_IF_SUCCESS = "\"Else If\" condition matched to proceed with the steps under this condition.";
  public final static String MSG_CONDITION_ELSE_SUCCESS = "\"Else\" condition matched to proceed with the steps under this condition.";
  public static final String EXCEPTION_WEBDRIVER_NOTCREATED = "Unable to create a new Test Session due to unexpected failure(0x537). Please contact Support for more details.";
  public static final String NO_PARALLEL_RUNS = "Parallel Executions Limit exceeded.Please upgrade to community edition for more parallel runs or Please contact support team for more details.";
  public static final String EXCEPTION_INVALID_PARAMETER_FORMAT = "Invalid value ?1 entered for parameter ?2 while executing the Custom Test Data Function \"?3\"";
  public static final String EXCEPTION_INVALID_CLASS_NAME = "Unsupported class \"?1\" used to generate test data from custom function";
  public static final String EXCEPTION_METHOD_NOT_FOUND = "No implementation found for this template";
  public static final String EXCEPTION_INVALID_TESTDATA = "No data available for runtime test data variable ?1. Refer previous Test Steps in this Test Case or Test Steps in other Test Cases to know the variable names saved by using store(naturalText) action Test Steps. Go to https://testsigma.com/docs/test-data/types/runtime/ to know more about runtime test data.";
  public static final String MSG_INVALID_URL = "When the given API Endpoint is invalid: UnknownHostException:Message : \"The given API URL  is not valid or the server is down.Please check whether the API URL is correct, starts with correct protocol (Http/Https) and also confirm the server is up and running\"";
  public static final String MSG_RESPONSE_EMPTY = "Property/Entity not found for given JSONPath expression : PathNotFoundException:Message : Property  for the given JSONPath  is not found in the Response.Please check the Response Body and provide correct JSONPath expression.";
  public static final String MSG_INVALID_PATH = "Property/Entity not found for given JSONPath expression : PathNotFoundException:Message : Property  for the given JSONPath  is not found in the Response.Please check the Response Body and provide correct JSONPath expression.";
  public static final String MSG_INVALID_RESPONSE = "The response from the given endpoint URL is not in JSON format. Unable to compare the response with the expected JSON data. You may check the complete Response Body in response section";
  public static final String MSG_REST_ERROR_STATUS = "Response status is not equal to the expected response";
  public static final String MSG_REST_ERROR_HEADERS = "Response headers doesn't match with the expected headers";
  public static final String MSG_REST_ERROR_CONTENT = "Response content doesn't match with the expected content";
  public static final String MSG_REST_ERROR_PATH = "Content for path ?1 doesn't match with the expected value";
  public static final String MSG_REST_ERROR_BODY_EMPTY = "No Test body provided to validate response";
  public static final String MSG_REST_SCHEMA_ERROR_EMPTY = "No schema provided to validate response";
  // Used in ActionConstants check for dependencies.

  public static final String KEYWORD_GO_TO = "Navigate to";
  public static final String KEYWORD_SCREENSHOT = "Take screenshot with URL";
  public static final String EXCEPTION_DOWNLOAD_LOCAL_FILE = "There is no file with URL ?1";
  final public static String INVALID_NUMBER_ARGUMENT = "Invalid integer provided to generate random test data string. Refer \"https://testsigma.com/docs/test-data/types/random/\" to know the valid integer range and learn more about random test data";
  public static final String MSG_REST_INVALID_URL = "Invalid endpoint URL specified. Make sure the target host in the URL is valid and accessible. Also, the URL begins with HTTP or HTTPS";
  public static final String NO_TESTCASES_AVAILABLE = "No testcases available in this environment";
  public static final String DEVICE_NOT_FOUND = "The associated device ?1 is available. Please check if its connected " +
    "or not.";
  public static final String FAILED_TO_FETCH_TEST_CASE_DETAILS = "Error while fetching test case details";
  public final static String MSG_WHILE_CONDITION_FAILED = "Loop condition failed, exiting from While loop execution.";
  public final static String MSG_WHILE_LOOP_SUCCESS = "While loop executed successfully";
  public final static String MSG_WHILE_LOOP_FAILURE = "While loop execution failed";
  public final static String MSG_WHILE_LOOP_ITERATIONS_EXHAUSTED = "Max. number of allowed iterations reached before achieving the expected functional behaviour";
  public static String msgParamIdentifier = "\\?";

  public static String getMessage(String replaceableMsg, Object... replaceParameters) {
    replaceableMsg = replaceableMsg + " ";
    if ((replaceParameters == null) || (replaceParameters.length < 1))
      return replaceableMsg;
    int paramSize = replaceParameters.length;
    for (int i = 0; i < paramSize; i++) {
      String[] replacebleMsg = replaceableMsg.split(msgParamIdentifier + (i + 1));
      String tempStr = replacebleMsg[0];
      int replaceLength = replacebleMsg.length;
      for (int j = 1; j < replaceLength; j++) {
        String repMsg = replacebleMsg[j];
        String parm = (replaceParameters[i] != null) ? replaceParameters[i].toString() : "";
        tempStr = tempStr + parm + repMsg;
      }
      replaceableMsg = tempStr;
    }
    return replaceableMsg;
  }

}
