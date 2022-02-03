package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.support.ui.Select;

public class StoreOptionsCountAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved options count in a run time variable.<br><b>%s=%s</b>";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    int runTimeVarValue = selectElement.getOptions().size();
    runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue + "");
    resultMetadata.put(getTestData(), runTimeVarValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult(), runTimeVarValue));
  }
}
