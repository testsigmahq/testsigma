package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.actions.ActionsAction;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

public class ClickOnElementAction extends ActionsAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed click action.";

  @Override
  protected void execute() throws Exception {
    click(TESTS_TEP_DATA_MAP_KEY_ELEMENT, true);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
