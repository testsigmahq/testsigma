package com.testsigma.automator.actions.mobile.mobileweb.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.web.wait.WaitUntilElementProxyAction;


public class WaitUntilElementMobileWebProxyAction extends WaitUntilElementProxyAction {

  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.VISIBLE:
        WaitUntilElementIsVisibleAction visible = (WaitUntilElementIsVisibleAction) this.initializeChildSnippet(WaitUntilElementIsVisibleAction.class);
        visible.execute();
        this.setSuccessMessage(visible.getSuccessMessage());
        break;
      case ActionConstants.NOT_VISIBLE:
        WaitUntilElementIsNotVisibleAction notVisible = (WaitUntilElementIsNotVisibleAction) this.initializeChildSnippet(WaitUntilElementIsNotVisibleAction.class);
        notVisible.execute();
        this.setSuccessMessage(notVisible.getSuccessMessage());
        break;
      case ActionConstants.SELECTED:
        WaitUntilElementIsSelectedAction selectedNlp = (WaitUntilElementIsSelectedAction) this.initializeChildSnippet(WaitUntilElementIsSelectedAction.class);
        selectedNlp.execute();
        this.setSuccessMessage(selectedNlp.getSuccessMessage());
        break;
      case ActionConstants.NOT_SELECTED:
        WaitUntilElementIsNotSelectedAction notSelectedNlp = (WaitUntilElementIsNotSelectedAction) this.initializeChildSnippet(WaitUntilElementIsNotSelectedAction.class);
        notSelectedNlp.execute();
        this.setSuccessMessage(notSelectedNlp.getSuccessMessage());
        break;
      case ActionConstants.CLICKABLE:
        WaitUntilElementIsClickableAction clickable = (WaitUntilElementIsClickableAction) this.initializeChildSnippet(WaitUntilElementIsClickableAction.class);
        clickable.execute();
        this.setSuccessMessage(clickable.getSuccessMessage());
        break;
      case ActionConstants.ENABLED:
        WaitUntilElementIsEnabledAction enabled = (WaitUntilElementIsEnabledAction) this.initializeChildSnippet(WaitUntilElementIsEnabledAction.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case ActionConstants.DISABLED:
        WaitUntilElementIsDisabledAction disabled = (WaitUntilElementIsDisabledAction) this.initializeChildSnippet(WaitUntilElementIsDisabledAction.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Wait Action due to error at test data");
        throw new AutomatorException("Unable to Perform Wait Action due to error at test data");
    }
  }

}
