package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;

public class StoreTagNameAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved element tag name in a run time variable.<br><b>%s=%s</b>";

  @Override
  protected void execute() throws Exception {
    findElement();
    String runTimeVarValue = getElement().getTagName();
    runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue);
    resultMetadata.put(getTestData(), runTimeVarValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
  }
}
