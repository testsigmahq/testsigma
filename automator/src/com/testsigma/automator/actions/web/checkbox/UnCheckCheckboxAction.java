package com.testsigma.automator.actions.web.checkbox;

public class UnCheckCheckboxAction extends CheckBoxAction {
  private static final String SUCCESS_MESSAGE_CHECKBOX_UNCHECKED = "Successfully Un-checked the checkbox with element locator %s:%s.";
  private static final String FAILURE_MESSAGE_NOT_UNCHECKED = "Unable to remove the checkbox. " +
    "Please verify if the checkbox with locator <b>\"%s:%s\"</b> is enabled for select action.";
  private static final String ELEMENT_NOT_FOUND_FAILED_MESSAGE = "Element not found with search criteria <b>\"%s:%s\"</b>";

  @Override
  public void execute() throws Exception {
    uncheck();
  }

  protected String getUncheckSucceededMessage() {
    return String.format(SUCCESS_MESSAGE_CHECKBOX_UNCHECKED, getFindByType(), getLocatorValue());
  }

  protected String getUncheckFailedMessage() {
    return String.format(FAILURE_MESSAGE_NOT_UNCHECKED, getFindByType(), getLocatorValue());
  }

  protected String getElementNotFoundMessage() {
    return String.format(ELEMENT_NOT_FOUND_FAILED_MESSAGE, getFindByType(), getLocatorValue());
  }
}
