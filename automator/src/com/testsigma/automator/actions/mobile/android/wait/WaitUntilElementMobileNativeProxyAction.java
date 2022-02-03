package com.testsigma.automator.actions.mobile.android.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.web.wait.WaitUntilElementProxyAction;


public class WaitUntilElementMobileNativeProxyAction extends WaitUntilElementProxyAction {

  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.VISIBLE:
        WaitUntilElementIsVisibleSnippet visible = (WaitUntilElementIsVisibleSnippet) this.initializeChildSnippet(WaitUntilElementIsVisibleSnippet.class);
        visible.execute();
        this.setSuccessMessage(visible.getSuccessMessage());
        break;
      case ActionConstants.NOT_VISIBLE:
        WaitUntilElementIsNotVisibleSnippet notVisible = (WaitUntilElementIsNotVisibleSnippet) this.initializeChildSnippet(WaitUntilElementIsNotVisibleSnippet.class);
        notVisible.execute();
        this.setSuccessMessage(notVisible.getSuccessMessage());
        break;
      case ActionConstants.SELECTED:
        WaitUntilElementIsSelectedSnippet selectedNlp = (WaitUntilElementIsSelectedSnippet) this.initializeChildSnippet(WaitUntilElementIsSelectedSnippet.class);
        selectedNlp.execute();
        this.setSuccessMessage(selectedNlp.getSuccessMessage());
        break;
      case ActionConstants.NOT_SELECTED:
        WaitUntilElementIsNotSelectedSnippet notSelectedNlp = (WaitUntilElementIsNotSelectedSnippet) this.initializeChildSnippet(WaitUntilElementIsNotSelectedSnippet.class);
        notSelectedNlp.execute();
        this.setSuccessMessage(notSelectedNlp.getSuccessMessage());
        break;
      case ActionConstants.CLICKABLE:
        WaitUntilElementIsClickableSnippet clickable = (WaitUntilElementIsClickableSnippet) this.initializeChildSnippet(WaitUntilElementIsClickableSnippet.class);
        clickable.execute();
        this.setSuccessMessage(clickable.getSuccessMessage());
        break;
      case ActionConstants.ENABLED:
        WaitUntilElementIsEnabledSnippet enabled = (WaitUntilElementIsEnabledSnippet) this.initializeChildSnippet(WaitUntilElementIsEnabledSnippet.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case ActionConstants.DISABLED:
        WaitUntilElementIsDisabledSnippet disabled = (WaitUntilElementIsDisabledSnippet) this.initializeChildSnippet(WaitUntilElementIsDisabledSnippet.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Wait Action due to error at test data");
        throw new AutomatorException("Unable to Perform Wait Action due to error at test data");
    }
  }

}
