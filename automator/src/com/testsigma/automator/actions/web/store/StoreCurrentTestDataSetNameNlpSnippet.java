package com.testsigma.automator.actions.web.store;


import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.exceptions.AutomatorException;
import org.apache.commons.lang3.StringUtils;

public class StoreCurrentTestDataSetNameNlpSnippet extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully test data set name is saved to run time variable<br><b>%s</b>";
  private static final String ERROR_MESSAGE = "Invalid TestData set name <b>%s</b>";
  private static final String ERROR_TEST_CASE_MESSAGE = "Test data profile is not associated";


  @Override
  protected void execute() throws Exception {
    if(this.getTestDataProfileEntity().getTestDataSetName() == null){
      throw new AutomatorException(String.format(ERROR_TEST_CASE_MESSAGE));
    }

    if(StringUtils.isEmpty(this.getTestDataProfileEntity().getTestDataSetName())){
      throw new AutomatorException(String.format(ERROR_MESSAGE, this.getTestDataProfileEntity().getTestDataSetName()));
    }
    runtimeDataProvider.storeRuntimeVariable(getAttribute(), this.getTestDataProfileEntity().getTestDataSetName());
    resultMetadata.put(getAttribute(), this.getTestDataProfileEntity().getTestDataSetName());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, this.getTestDataProfileEntity().getTestDataSetName()));
  }
}
