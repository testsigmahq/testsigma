package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ActionsAction;

public class SubmitFormAction extends ActionsAction {

  private static final String SUCCESS_MESSAGE = "Successfully submitted the form.";
  private static final String FAILURE_MESSAGE = "unable to submit the form. " +
    "Please verify that the given locator <b>\"%s:%s\"</b> is present in the form";

  @Override
  protected void execute() throws Exception {

    findElement();
    try {
      getElement().submit();
    } catch (Exception e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
