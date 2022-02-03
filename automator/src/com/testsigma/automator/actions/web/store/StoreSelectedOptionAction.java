package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.support.ui.Select;

public class StoreSelectedOptionAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully saved Selected option from given locator in a run time variable.<br><b>%s=%s</b>";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    String runTimeVarValue = selectElement.getFirstSelectedOption().getText();
    runtimeDataProvider.storeRuntimeVariable(getTestData(), runTimeVarValue);
    resultMetadata.put(getTestData(), runTimeVarValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), runTimeVarValue));
  }
}
