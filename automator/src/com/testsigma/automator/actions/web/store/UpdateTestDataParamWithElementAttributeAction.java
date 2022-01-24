package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA;

public class UpdateTestDataParamWithElementAttributeAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved attribute value in a test data parameter.<br><b>%s=%s</b>";
  private static final String FAILURE_EMPTY_VALUE = "The attribute <b>\"%s\"</b> value for given element is empty. Please provide valid attribute " +
    " and locator.<br>Updating testdata parameter with empty value may result in unwanted failures in tests where it is being used.";

  @Override
  protected void execute() throws Exception {
    findElement();
    String attributeValue = getElement().getAttribute(getAttribute());
    Assert.isTrue(!(StringUtils.isEmpty(attributeValue)), String.format(FAILURE_EMPTY_VALUE, getAttribute()));
    String testdataName = getTestDataPropertiesEntity(TEST_STEP_DATA_MAP_KEY_TEST_DATA).getTestDataName();
    testDataParams.put(testdataName, attributeValue.trim());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, testdataName, attributeValue.trim()));
  }
}
