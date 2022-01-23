package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;

public class StoreCurrentURLAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved URL value in a run time variable.<br><b>%s=%s</b>";

  @Override
  protected void execute() throws Exception {
    String runTimeVarValue = getDriver().getCurrentUrl();
    runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue);
    resultMetadata.put(getTestData(), runTimeVarValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult(), runTimeVarValue));
  }
}
