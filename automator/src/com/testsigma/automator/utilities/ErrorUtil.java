package com.testsigma.automator.utilities;

import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.SessionErrorType;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.exceptions.TestsigmaNoParallelRunException;

public class ErrorUtil {

  public static final String EXCEPTION_INVALID_DEVICES = "Only a limited set of devices are available in the selected Plan. To select any of the available devices, please upgrade to a higher plan";
  public static final String EXCEPTION_INVALID_BROWSERS = "Only latest Browser versions are available in the selected Plan. To select older Browser versions,please upgrade to a higher plan";
  public static final String EXCEPTION_REST_AUTOMATION = "To execute Rest API Tests, please upgrade to a higher plan";
  public static final String EXCEPTION_MOBILE_AUTOMATION = "To execute Mobile Application Tests, please upgrade to a higher plan";
  public static final String MSG_NO_PARALLEL_RUN = "Parallel Executions Limit exceeded.Please upgrade to community edition for more parallel runs.";
  public static final String UNKOWN_ERROR = "Unexpected error occurred";
  public static final String BROWSER_VERSION_NOT_AVILABLE = "Selected Browser Version is not available in your Local.";

  public void checkError(Integer error, String message) throws AutomatorException {

    if (error == null && message == null) {
      return;
    }

    if (ErrorCodes.UNLIMITED_AUTOMATION_BROWSERS.equals(error)) {
      throw new AutomatorException(ErrorCodes.UNLIMITED_AUTOMATION_BROWSERS,
        EXCEPTION_INVALID_BROWSERS);
    } else if (ErrorCodes.UNLIMITED_AUTOMATION_DEVICES.equals(error)) {
      throw new AutomatorException(ErrorCodes.UNLIMITED_AUTOMATION_DEVICES,
        EXCEPTION_INVALID_DEVICES);
    } else if (ErrorCodes.MOBILE_AUTOMATION.equals(error)) {
      throw new AutomatorException(ErrorCodes.UNLIMITED_AUTOMATION_DEVICES,
        EXCEPTION_MOBILE_AUTOMATION);
    } else if (ErrorCodes.REST_AUTOMATION.equals(error)) {
      throw new AutomatorException(ErrorCodes.UNLIMITED_AUTOMATION_DEVICES,
        EXCEPTION_REST_AUTOMATION);
    } else if (ErrorCodes.ERROR_MINS_VALIDATION_FAILURE.equals(error)) {
      throw new AutomatorException(error, message);
    } else if (ErrorCodes.ERROR_ELEMENT_FAILURE.equals(error)) {
      throw new AutomatorException(error, message);
    } else if (ErrorCodes.BROWSER_VERSION_NOT_AVAILABLE.equals(error)) {
      throw new AutomatorException(ErrorCodes.BROWSER_VERSION_NOT_AVAILABLE,
              BROWSER_VERSION_NOT_AVILABLE);
    } else if (ErrorCodes.NO_PARALLEL_RUN.equals(error)) {
      throw new TestsigmaNoParallelRunException(ErrorCodes.NO_PARALLEL_RUN,
              MSG_NO_PARALLEL_RUN);
    } else {
      message = (message != null) ? message : UNKOWN_ERROR;
      throw new AutomatorException(error, message);
    }
  }
}
