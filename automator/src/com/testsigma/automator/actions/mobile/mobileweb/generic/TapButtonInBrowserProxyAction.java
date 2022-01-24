package com.testsigma.automator.actions.mobile.mobileweb.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.web.generic.ClickOnButtonInTheBrowserProxyAction;

public class TapButtonInBrowserProxyAction extends ClickOnButtonInTheBrowserProxyAction {
  @Override
  public void execute() throws Exception {
    String button = getTestData();
    switch (button) {
      case ActionConstants.REFRESH:
        ReLoadCurrentPageAction refresh = (ReLoadCurrentPageAction) this.initializeChildSnippet(ReLoadCurrentPageAction.class);
        refresh.execute();
        this.setSuccessMessage(refresh.getSuccessMessage());
        break;
      case ActionConstants.BACK:
        NavigateBackAction back = (NavigateBackAction) this.initializeChildSnippet(NavigateBackAction.class);
        back.execute();
        this.setSuccessMessage(back.getSuccessMessage());
        break;
      case ActionConstants.FORWARD:
        NavigateForwardAction forward = (NavigateForwardAction) this.initializeChildSnippet(NavigateForwardAction.class);
        forward.execute();
        this.setSuccessMessage(forward.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Tap on Button in the Browser due to error at test data");
        throw new AutomatorException("Unable to Tap on Button in the Browser due to error at test data");
    }
  }


}

