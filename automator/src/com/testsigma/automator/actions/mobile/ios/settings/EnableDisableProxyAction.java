package com.testsigma.automator.actions.mobile.ios.settings;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.ios.swipe.IOSSwipeElementProxyAction;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EnableDisableProxyAction extends IOSSwipeElementProxyAction {


  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.ENABLE:
        EnableWIFIAction enable = (EnableWIFIAction) this.initializeChildSnippet(EnableWIFIAction.class);
        enable.execute();
        this.setSuccessMessage(enable.getSuccessMessage());
        break;
      case ActionConstants.DISABLE:
        DisableWIFIAction disable = (DisableWIFIAction) this.initializeChildSnippet(DisableWIFIAction.class);
        disable.execute();
        this.setSuccessMessage(disable.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Enable/Disable Action due to error at test data");
        throw new AutomatorException("Unable to Perform Enable/Disable Action due to error at test data");
    }
  }

}
