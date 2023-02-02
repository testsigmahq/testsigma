package com.testsigma.automator.actions.mobile.mobileweb.press;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.web.verify.VerifyElementProxyAction;


public class PressMobileWebKeyProxyAction extends VerifyElementProxyAction {
  @Override
  public void execute() throws Exception {
    String key = getTestData(NaturalTextActionConstants.TEST_DATA_SPACE_KEY);
    switch (key) {
      case ActionConstants.SPACE:
        MobileWebPressSpaceAction space = (MobileWebPressSpaceAction) this.initializeChildSnippet(MobileWebPressSpaceAction.class);
        space.execute();
        this.setSuccessMessage(space.getSuccessMessage());
        break;
      case ActionConstants.ENTER:
        MobileWebPressEnterAction enter = (MobileWebPressEnterAction) this.initializeChildSnippet(MobileWebPressEnterAction.class);
        enter.execute();
        this.setSuccessMessage(enter.getSuccessMessage());
        break;
      case ActionConstants.BACKSPACE:
        MobileWebPressBackSpaceAction backSpace = (MobileWebPressBackSpaceAction) this.initializeChildSnippet(MobileWebPressBackSpaceAction.class);
        backSpace.execute();
        this.setSuccessMessage(backSpace.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Press Key Action due to error at test data");
        throw new AutomatorException("Unable to Press Key Action due to error at test data");
    }
  }


}

