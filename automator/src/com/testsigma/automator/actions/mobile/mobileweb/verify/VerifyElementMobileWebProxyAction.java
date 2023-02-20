package com.testsigma.automator.actions.mobile.mobileweb.verify;

import com.testsigma.automator.actions.web.verify.VerifyElementProxyAction;
import com.testsigma.automator.constants.NaturalTextActionConstants;

public class VerifyElementMobileWebProxyAction extends VerifyElementProxyAction {
  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case NaturalTextActionConstants.DISPLAYED:
      case NaturalTextActionConstants.VISIBLE:
        VerifyElementPresentAction present = (VerifyElementPresentAction) this.initializeChildSnippet(VerifyElementPresentAction.class);
        present.execute();
        this.setSuccessMessage(present.getSuccessMessage());
        break;
      case NaturalTextActionConstants.NOT_PRESENT:
        VerifyElementAbsenceAction absence = (VerifyElementAbsenceAction) this.initializeChildSnippet(VerifyElementAbsenceAction.class);
        absence.execute();
        this.setSuccessMessage(absence.getSuccessMessage());
        break;
      case NaturalTextActionConstants.NOT_DISPLAYED:
      case NaturalTextActionConstants.NOT_VISIBLE:
        VerifyElementIsNotDisplayedAction notDisplayed = (VerifyElementIsNotDisplayedAction) this.initializeChildSnippet(VerifyElementIsNotDisplayedAction.class);
        notDisplayed.execute();
        this.setSuccessMessage(notDisplayed.getSuccessMessage());
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
        VerifyDisabledAction disabled = (VerifyDisabledAction) this.initializeChildSnippet(VerifyDisabledAction.class);
        disabled.execute();
        this.setSuccessMessage(disabled.getSuccessMessage());
        break;
      case NaturalTextActionConstants.CHECKED:
        VerifyCheckedAction checked = (VerifyCheckedAction) this.initializeChildSnippet(VerifyCheckedAction.class);
        checked.execute();
        this.setSuccessMessage(checked.getSuccessMessage());
        break;
      case NaturalTextActionConstants.UN_CHECKED:
        VerifyUncheckedAction unChecked = (VerifyUncheckedAction) this.initializeChildSnippet(VerifyUncheckedAction.class);
        unChecked.execute();
        this.setSuccessMessage(unChecked.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Verify Action due to error at test data");
        throw new Exception("Unable to Perform Verify Action due to error at test data");
    }
  }
}
