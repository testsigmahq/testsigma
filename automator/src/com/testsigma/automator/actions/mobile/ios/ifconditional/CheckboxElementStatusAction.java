package com.testsigma.automator.actions.mobile.ios.ifconditional;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.ios.switchactions.SwitchEnableDisableProxyAction;
import com.testsigma.automator.actions.mobile.ios.verify.VerifySwitchDisabledAction;
import com.testsigma.automator.actions.mobile.ios.verify.VerifySwitchEnabledAction;

public class CheckboxElementStatusAction extends SwitchEnableDisableProxyAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.CHECKED:
        VerifySwitchEnabledAction enable = (VerifySwitchEnabledAction) this.initializeChildSnippet(VerifySwitchEnabledAction.class);
        enable.execute();
        this.setSuccessMessage(enable.getSuccessMessage());
        break;
      case ActionConstants.UN_CHECKED:
        VerifySwitchDisabledAction disable = (VerifySwitchDisabledAction) this.initializeChildSnippet(VerifySwitchDisabledAction.class);
        disable.execute();
        this.setSuccessMessage(disable.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Switch Status Action due to error at test data");
        throw new AutomatorException("Unable to Perform Verify Switch Status Action due to error at test data");
    }
  }
}
