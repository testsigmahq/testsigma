package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class StoreElementAttributeAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved attribute value in a run time variable.<br><b>%s=%s</b>";
  private static final String FAILURE_MESSAGE = "The attribute <b>\"%s\"</b> value for given element is empty. Please provide valid attribute and locator.";

  @Override
  protected void execute() throws Exception {
    findElement();
    String attributeValue = getElement().getAttribute(getAttribute());
    Assert.isTrue(!(StringUtils.isEmpty(attributeValue)), String.format(FAILURE_MESSAGE, getAttribute()));
    runtimeDataProvider.storeRuntimeVariable(getTestData(), attributeValue);
    resultMetadata.put(getTestData(), attributeValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), attributeValue));
  }
}
