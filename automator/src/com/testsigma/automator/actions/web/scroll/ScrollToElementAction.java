package com.testsigma.automator.actions.web.scroll;

import com.testsigma.automator.actions.ActionsAction;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

public class ScrollToElementAction extends ActionsAction {
  @Override
  protected void execute() throws Exception {
    scrollToElement(TESTS_TEP_DATA_MAP_KEY_ELEMENT);
    setSuccessMessage("Successfully scrolled to element");
  }
}
