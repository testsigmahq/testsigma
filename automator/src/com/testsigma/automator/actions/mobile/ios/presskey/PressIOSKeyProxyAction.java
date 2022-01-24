package com.testsigma.automator.actions.mobile.ios.presskey;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.mobileweb.press.PressMobileWebKeyProxyAction;


public class PressIOSKeyProxyAction extends PressMobileWebKeyProxyAction {
  @Override
  public void execute() throws Exception {
    String key = getTestData();
    switch (key) {
      case ActionConstants.SPACE:
        PressSpaceKeyAction space = (PressSpaceKeyAction) this.initializeChildSnippet(PressSpaceKeyAction.class);
        space.execute();
        this.setSuccessMessage(space.getSuccessMessage());
        break;
      case ActionConstants.ENTER:
        PressEnterKeyAction enter = (PressEnterKeyAction) this.initializeChildSnippet(PressEnterKeyAction.class);
        enter.execute();
        this.setSuccessMessage(enter.getSuccessMessage());
        break;
      case ActionConstants.BACKSPACE:
        PressBackSpaceKeyAction backSpace = (PressBackSpaceKeyAction) this.initializeChildSnippet(PressBackSpaceKeyAction.class);
        backSpace.execute();
        this.setSuccessMessage(backSpace.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Tap Keys Action due to error at test data");
        throw new AutomatorException("Unable to Tap Keys Action due to error at test data");
    }
  }
}

