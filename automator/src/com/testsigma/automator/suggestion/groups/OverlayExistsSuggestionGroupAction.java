package com.testsigma.automator.suggestion.groups;

import com.testsigma.automator.suggestion.actions.SuggestionAction;
import com.testsigma.automator.suggestion.actions.SuggestionActionResult;
import com.testsigma.automator.suggestion.actions.web.CloseOverlayAction;
import com.testsigma.automator.suggestion.actions.web.OverlayExistsAction;

public class OverlayExistsSuggestionGroupAction extends SuggestionAction {
  @Override
  public void execute() throws Exception {
    new OverlayExistsAction().run();
    new CloseOverlayAction().run();
    this.suggestionActionResult = SuggestionActionResult.Success;
  }
}
