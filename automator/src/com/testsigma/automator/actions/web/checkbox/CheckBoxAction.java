package com.testsigma.automator.actions.web.checkbox;

import com.testsigma.automator.actions.ActionsAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

@Log4j2
public abstract class CheckBoxAction extends ActionsAction {
  private static final String SUCCESS_MESSAGE_SELECTED = "Successfully selected the checkbox with locator <b>\"%s:%s\"</b>";
  private static final String SUCCESS_MESSAGE_UNCHECKED = "Successfully unchecked the checkbox with locator <b>\"%s:%s\"</b>";
  private static final String SUCCESS_MESSAGE_ALREADY_SELECTED = "Checkbox is already in selected state";
  private static final String SUCCESS_MESSAGE_ALREADY_UNCHECKED = "Checkbox is already in unchecked state";
  private static final String ELEMENT_NOT_FOUND_FAILED_MESSAGE = "Element not found with search criteria <b>\"%s:%s\"</b>";
  private static final String FAILURE_MESSAGE_NOT_SELECTED = "Unable to select the checkbox. " +
    "Please verify if the checkbox with locator <b>\"%s:%s\"</b> is enabled for check/select action.";
  private static final String FAILURE_MESSAGE_NOT_UNCHECKED = "Unable to remove the checkbox selection. " +
    "Please verify if the checkbox with locator <b>\"%s:%s\"</b> is enabled for check/select action.";

  protected void check() throws Exception {
    findElement();
    validateElementType(ActionConstants.ELEMENT_TYPE_CHECKBOX);
    if (getElement().isSelected()) {
      setSuccessMessage(SUCCESS_MESSAGE_ALREADY_SELECTED);
      return;
    }
    click(TESTS_TEP_DATA_MAP_KEY_ELEMENT, true);
    Assert.isTrue(getElement().isSelected(), getSelectFailedMessage());
    setSuccessMessage(getSelectSucceededMessage());
  }

  protected void uncheck() throws Exception {
    findElement();
    validateElementType(ActionConstants.ELEMENT_TYPE_CHECKBOX);
    if (!getElement().isSelected()) {
      setSuccessMessage(SUCCESS_MESSAGE_ALREADY_UNCHECKED);
      return;
    }
    click(TESTS_TEP_DATA_MAP_KEY_ELEMENT, true);
    Assert.isTrue(!getElement().isSelected(), getUncheckFailedMessage());
    setSuccessMessage(getUncheckSucceededMessage());
  }

  @Override
  protected abstract void execute() throws Exception;

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    updateErrorMessageForDynamicLocatorTypes(e, getElementNotFoundMessage());
  }

  protected String getSelectSucceededMessage() {
    return String.format(SUCCESS_MESSAGE_SELECTED, getFindByType(), getLocatorValue());
  }

  protected String getSelectFailedMessage() {
    return String.format(FAILURE_MESSAGE_NOT_SELECTED, getFindByType(), getLocatorValue());
  }

  protected String getUncheckSucceededMessage() {
    return String.format(SUCCESS_MESSAGE_UNCHECKED, getFindByType(), getLocatorValue());
  }

  protected String getUncheckFailedMessage() {
    return String.format(FAILURE_MESSAGE_NOT_UNCHECKED, getFindByType(), getLocatorValue());
  }

  protected String getElementNotFoundMessage() {
    return String.format(ELEMENT_NOT_FOUND_FAILED_MESSAGE, getFindByType(), getLocatorValue());
  }
}
