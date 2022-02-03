package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;

public class GetTextFromElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE_WITH_DATA = "Below is the text from given element::<br>%s";

  @Override
  protected void execute() throws Exception {
    findElement();
    setSuccessMessage(String.format(SUCCESS_MESSAGE_WITH_DATA, getElement().getText()));
  }
}
