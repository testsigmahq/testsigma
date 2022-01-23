package com.testsigma.automator.actions.mobile.android.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.swipe.MobileNativeSwipeElementProxyAction;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class EnableDisableProxyAction extends MobileNativeSwipeElementProxyAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.ENABLE:
        EnableSwitchSnippet enable = (EnableSwitchSnippet) this.initializeChildSnippet(EnableSwitchSnippet.class);
        enable.execute();
        this.setSuccessMessage(enable.getSuccessMessage());
        break;
      case ActionConstants.DISABLE:
        DisableSwitchAction disable = (DisableSwitchAction) this.initializeChildSnippet(DisableSwitchAction.class);
        disable.execute();
        this.setSuccessMessage(disable.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Enable/Disable Action due to error at test data");
        throw new AutomatorException("Unable to Perform Enable/Disable Action due to error at test data");
    }
  }

}
