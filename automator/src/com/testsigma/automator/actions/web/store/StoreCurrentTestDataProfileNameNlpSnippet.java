package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.exceptions.AutomatorException;
import org.apache.commons.lang3.StringUtils;

public class StoreCurrentTestDataProfileNameNlpSnippet extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully test data profile name is saved into run time variable<br><b>%s</b>";
  private static final String ERROR_MESSAGE = "Invalid TestData profile is selected  <b>%s</b>";

  @Override
  protected void execute() throws Exception {
    if(StringUtils.isEmpty(this.getTestDataProfileEntity().getTestDataProfile())){
      throw new AutomatorException(String.format(ERROR_MESSAGE, this.getTestDataProfileEntity().getTestDataProfile()));
    }
    runtimeDataProvider.storeRuntimeVariable(getAttribute(), this.getTestDataProfileEntity().getTestDataProfile());
    resultMetadata.put(getAttribute(), this.getTestDataProfileEntity().getTestDataProfile());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, this.getTestDataProfileEntity().getTestDataProfile()));
  }
}
