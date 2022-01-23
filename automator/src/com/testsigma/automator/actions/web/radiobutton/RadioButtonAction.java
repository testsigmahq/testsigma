package com.testsigma.automator.actions.web.radiobutton;

import com.testsigma.automator.actions.ActionsAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

@Log4j2
public abstract class RadioButtonAction extends ActionsAction {
  private static final String SUCCESS_MESSAGE_SELECTED = "Successfully selected the radio button with locator <b>\"%s:%s\"</b>";
  private static final String ELEMENT_NOT_FOUND_FAILED_MESSAGE = "Element not found with search criteria <b>\"%s:%s\"</b>";
  private static final String FAILURE_MESSAGE_NOT_SELECTED = "Unable to select the radiobutton. " +
    "Please verify if the radiobutton with locator <b>\"%s:%s\"</b> is enabled for select action.";
  private static final String SUCCESS_MESSAGE_ALREADY_SELECTED = "Radio button is already in selected state";

  protected void select() throws Exception {
    findElement();
    validateElementType(ActionConstants.ELEMENT_TYPE_RADIO);
    if (getElement().isSelected()) {
      setSuccessMessage(SUCCESS_MESSAGE_ALREADY_SELECTED);
      return;
    }
    click(TESTS_TEP_DATA_MAP_KEY_ELEMENT, true);
    Assert.isTrue(getElement().isSelected(), getSelectFailedMessage());
    setSuccessMessage(getSelectSucceededMessage());
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

  protected String getElementNotFoundMessage() {
    return String.format(ELEMENT_NOT_FOUND_FAILED_MESSAGE, getFindByType(), getLocatorValue());
  }
}
