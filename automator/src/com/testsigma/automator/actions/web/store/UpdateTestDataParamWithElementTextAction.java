package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA;

public class UpdateTestDataParamWithElementTextAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved element text in a test data parameter.<br><b>%s=%s</b>";
  private static final String FAILURE_EMPTY_VALUE = "The element text value for given element is empty." +
    "<br>Updating testdata parameter with empty value may result in unwanted failures in tests where it is being used.";

  @Override
  protected void execute() throws Exception {
    findElement();
    String value = getElement().getText().trim();
    Assert.isTrue(!(StringUtils.isEmpty(value)), FAILURE_EMPTY_VALUE);
    String testdataName = getTestDataPropertiesEntity(TEST_STEP_DATA_MAP_KEY_TEST_DATA).getTestDataName();
    testDataParams.put(testdataName, value);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, testdataName, value));
  }
}
