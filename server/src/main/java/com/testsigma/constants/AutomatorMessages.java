package com.testsigma.constants;

public class AutomatorMessages {

  public final static String MSG_RESPONSE_SIZE_EXCEEDS = "Response is too big to save";

  public final static String MSG_REST_STEP_PAYLOAD_SIZE_EXCEEDS = "Payload exceeds 1MB";
  public final static String MSG_USER_ABORTED_EXECUTION = "User stopped the Test Plan";
  public final static String MSG_TEST_PLAN_STOPPED = "Test Plan Execution Was Stopped";
  public static final String EXCEPTION_INVALID_PARAMETER_FORMAT = "Invalid value ?1 entered for parameter ?2 while executing the Custom Test Data Function \"?3\"";

  // Used in ActionConstants check for dependencies.
  final public static String EMPTY_TEST_DATA = "actionstep.message.empty.testdata.teststep";
  final public static String EMPTY_ELEMENT = "actionstep.message.empty.element.teststep";
  final public static String EMPTY_ATTRIBUTE = "actionstep.message.empty.attribute.teststep";
  final public static String EMPTY_FROM_ELEMENT = "actionstep.message.empty.fromelement.teststep";
  final public static String EMPTY_TO_ELEMENT = "actionstep.message.empty.toelement.teststep";

  public static final String MSG_EXECUTION_IN_PROGRESS = "Test Plan in Progress";
  public static final String MSG_EXECUTION_PRE_FLIGHT = "Test plan is moved to pre flight status";
  public static final String MSG_EXECUTION_QUEUED = "Test plan is queued";
  public static final String MSG_EXECUTION_CREATED = "Test Plan execution is initiated and waiting to be queued";
  public static final String MSG_ENVIRONMENT_COMPLETED = "Execution Environment Finished Running";
  public static final String MSG_ENVIRONMENT_FAILURE = "Failed While Running Execution Environment";
  public static final String MSG_EXECUTION_COMPLETED = "Test plan execution completed";
  public static final String MSG_EXECUTION_FAILURE = "Test plan execution failed";

  final public static String EXECUTION_ALREADY_RUNNING = "The specified Test Plan is currently running. Please wait until the current Test Plan is complete. You may also use Testsigma Test Plan Status Check API to wait for the current Test Plan to complete. More details - https://testsigma.com/docs/continuous-integration/rest-api/";

  public static final String AGENT_INACTIVE = "Agent is out of Sync. Please restart Agent";
  public static final String DEVICE_NOT_ONLINE = "Device is not online. Please check the device connection";

  public static final String RE_RUN_PARENT_ID_ALREADY_EXSISTS = "ReRun Parent Id Already Exists";
}
