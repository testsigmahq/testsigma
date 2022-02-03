package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.web.verify.VerifyElementProxyAction;
import com.testsigma.automator.constants.NaturalTextActionConstants;

public class VerifyElementMobileNativeProxyAction extends VerifyElementProxyAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case NaturalTextActionConstants.DISPLAYED:
      case NaturalTextActionConstants.VISIBLE:
        VerifyElementPresentSnippet present = (VerifyElementPresentSnippet) this.initializeChildSnippet(VerifyElementPresentSnippet.class);
        present.execute();
        this.setSuccessMessage(present.getSuccessMessage());
        break;
      case NaturalTextActionConstants.NOT_PRESENT:
      case NaturalTextActionConstants.NOT_VISIBLE:
        VerifyElementAbsentSnippet absence = (VerifyElementAbsentSnippet) this.initializeChildSnippet(VerifyElementAbsentSnippet.class);
        absence.execute();
        this.setSuccessMessage(absence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.PRESENT:
        VerifyElementIsAvailableAction available = (VerifyElementIsAvailableAction) this.initializeChildSnippet(VerifyElementIsAvailableAction.class);
        available.execute();
        this.setSuccessMessage(available.getSuccessMessage());
        break;
      case NaturalTextActionConstants.ENABLED:
        VerifyEnabledSnippet enabled = (VerifyEnabledSnippet) this.initializeChildSnippet(VerifyEnabledSnippet.class);
        enabled.execute();
        this.setSuccessMessage(enabled.getSuccessMessage());
        break;
      case NaturalTextActionConstants.DISABLED:
        VerifyDisabledSnippet disabled = (VerifyDisabledSnippet) this.initializeChildSnippet(VerifyDisabledSnippet.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Action due to error at test data");
        throw new Exception("Unable to Perform Verify Action due to error at test data");
    }
  }

}
