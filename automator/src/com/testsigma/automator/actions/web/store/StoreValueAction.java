package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class StoreValueAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved element text in a run time variable.<br><b>%s=%s</b>";
  private static final String FAILURE_MESSAGE = "The value <b>\"%s\"</b> displayed in given locator is empty. Please provide valid locator.";

  @Override
  protected void execute() throws Exception {
    findElement();
    String runTimeVarValue = getElement().getAttribute(ActionConstants.ATTRIBUTE_VALUE);
    Assert.isTrue(!(StringUtils.isEmpty(runTimeVarValue)), String.format(FAILURE_MESSAGE, getTestData()));
    runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue);
    resultMetadata.put(getTestData(), runTimeVarValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
  }
}
