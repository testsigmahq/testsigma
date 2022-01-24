package com.testsigma.automator.actions.mobile.ios.verify;

import com.testsigma.automator.actions.web.verify.VerifyElementProxyAction;
import com.testsigma.automator.constants.NaturalTextActionConstants;

public class VerifyElementIOSProxyAction extends VerifyElementProxyAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case NaturalTextActionConstants.DISPLAYED:
      case NaturalTextActionConstants.VISIBLE:
        VerifyElementPresenceAction present = (VerifyElementPresenceAction) this.initializeChildSnippet(VerifyElementPresenceAction.class);
        present.execute();
        this.setSuccessMessage(present.getSuccessMessage());
        break;
      case NaturalTextActionConstants.NOT_PRESENT:
      case NaturalTextActionConstants.NOT_VISIBLE:
        VerifyElementAbsenceAction absence = (VerifyElementAbsenceAction) this.initializeChildSnippet(VerifyElementAbsenceAction.class);
        absence.execute();
        this.setSuccessMessage(absence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.PRESENT:
        VerifyElementIsAvailableAction available = (VerifyElementIsAvailableAction) this.initializeChildSnippet(VerifyElementIsAvailableAction.class);
        available.execute();
        this.setSuccessMessage(available.getSuccessMessage());
        break;
      case NaturalTextActionConstants.ENABLED:
        VerifyElementEnabledAction enabled = (VerifyElementEnabledAction) this.initializeChildSnippet(VerifyElementEnabledAction.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case NaturalTextActionConstants.DISABLED:
        VerifyElementDisabledAction disabled = (VerifyElementDisabledAction) this.initializeChildSnippet(VerifyElementDisabledAction.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Action due to error at test data");
        throw new Exception("Unable to Perform Verify Action due to error at test data");
    }
  }
}
