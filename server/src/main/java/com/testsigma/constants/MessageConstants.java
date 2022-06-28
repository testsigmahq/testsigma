package com.testsigma.constants;

public class MessageConstants {
  public static final String MSG_UNKNOWN_TEST_DATA_DATA = "No Test data profile is mapped to Test case  ?1";
  public static final String REPORT_GENERATION_FAILED_TEST_PLAN_RUN_IS_NOT_COMPLETED = "Report generation failed, Test plan run is not completed";
  public static final String ELEMENT_WITH_THE_NAME_IS_NOT_AVAILABLE = "Element with the name \"?1\" is not available";
  public static final String MSG_UNKNOWN_ENVIRONMENT_PARAMETER_IN_TEST_STEP = "Environment test data variables \" ?1 \" used in Test Cases \" ?2 \" is not available in the associated Environments \" ?3\".  Take a look at the variable names added to the associated Environments and try again with the right variable name(case sensitive).";
  public static final String MSG_UNKNOWN_ENVIRONMENT_PARAMETER_IN_ELEMENT = "Environment variable with key \" ?1 \" used in Test Case \" ?2 \" is not available in the associated Element \" ?3\".  Take a look at the variable names added to the associated Environments and try again with the right variable name(case sensitive).";
  public static final String MSG_UNKNOWN_ENVIRONMENT_DATA_SET = "Environment variable type test data used in one or more Test Steps. But there is no Environment created or associated with this Execution. Click on Test Development -> Environments menu link to create a new Environments or select the Environments to use in this execution, if already available. Go to https://testsigma.freshdesk.com/support/solutions/articles/32000021868-how-to-use-global-parameter-test-data-in-test-steps- to know more about Environments.";
  public static final String MSG_UNKNOWN_TEST_DATA_PARAMETER_IN_TEST_STEP = "Test Data Parameter \"?1\" used in Test Case \"?2 \" is not available in the associated Test Data profile \" ?3 \".  Take a look at the parameter names added to the associated Test Data profile and try again with the right parameter name(case sensitive).";
  public static final String MSG_UNKNOWN_TEST_DATA_PARAMETER_NOTIN_TEST_STEP = "Test Data parameter \"?1\" used in this Test Step is not available in the associated Test Data profile \"?2\" .  Take a look at the parameter names added to the associated Test Data profile and try again with the right parameter name(case sensitive).";
  public static final String MSG_UNKNOWN_TEST_DATA_SET = "There is no Test Data profile created or associated with this Test Case to use parameter type test data in this Test Step. Click on Test Data menu link to create a new test data profile or edit this Test Case to associate data profile, if already available. ";
  public static final String MSG_UNKNOWN_TEST_DATA_DATA_DRIVEN_CASE = "There is no Test Data profile created or associated with this Test Case to use parameter type test data in this Test Step. Click on Test Data menu link to create a new test data profile or edit this Test Case to associate data profile, if already available. ";
  public static final String MSG_UNKNOWN_TEST_DATA_LOOP = "There is no Test Data profile created or associated with this loop or test data profile might be deleted. Click on Test Data menu link to create a new test data profile or edit this step to associate the correct data profile, if already available.";
  public static final String AGENT_DELETE_LINKED_ENVIRONMENTS = "Please unlink the following environments before attempting to delete this agent : ?1";
  public static final String INVALID_TEST_DATA = "Invalid Test Data name entered.";
  public static final String RUNTIME_DATA_VARIABLE_NOT_FOUND = "The runtime data variable requested is not found";
  public static final String UPDATE_TEST_STEP_RESULT = "Result updated from parent Step group status";
  public static final String TESTCASE_NOT_READY = "Test Case currently not in Ready state. Please change to Ready state before executing";
  public static final String STEP_GROUP_NOT_READY = "Step group ?1 currently not in Ready state. Please change to Ready state before executing";
  public static final String DRAFT_PLUGIN_ALLOWED_IN_HYBRID_ONLY = "Addon in draft mode is allowed to run only in local " +
    "test machine. Please publish your addon before running in cloud machines.";
  public static final String BACKUP_IS_IN_PROGRESS = "Backup is in progress";
  public static final String BACKUP_IS_SUCCESS = "Backup is completed";
  public static final String IMPORT_IS_IN_PROGRESS = "Import is in progress";
  public static final String IMPORT_IS_SUCCESS = "Import is completed";
  public static String TEST_PLAN_COMPLETED = "Test plan execution completed";
  public static String TEST_PLAN_FAILURE = "Test plan execution failed";
  public static String TEST_DATA_NOT_FOUND = "Test Step is Failed Because TestData parameter is not found %s with in selected step id Test data profile.";

  public static String getMessage(String replaceableMsg, Object... replaceParameters) {
    replaceableMsg = replaceableMsg + " ";
    if ((replaceParameters == null) || (replaceParameters.length < 1))
      return replaceableMsg;
    int paramSize = replaceParameters.length;
    for (int i = 0; i < paramSize; i++) {
      String[] replacebleMsg = replaceableMsg.split("\\?" + (i + 1));
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
