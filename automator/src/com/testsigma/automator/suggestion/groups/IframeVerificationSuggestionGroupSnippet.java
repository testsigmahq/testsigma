package com.testsigma.automator.suggestion.groups;

import com.testsigma.automator.suggestion.snippets.SuggestionSnippet;
import com.testsigma.automator.suggestion.snippets.SuggestionSnippetResult;
import com.testsigma.automator.suggestion.snippets.web.*;
import org.openqa.selenium.WebElement;

import java.util.List;

public class IframeVerificationSuggestionGroupSnippet extends SuggestionSnippet {
  @Override
  public void execute() throws Exception {
    List<WebElement> listOfFrames = (List<WebElement>) new ListFrameSnippet().runSnippet();
    for (WebElement frame : listOfFrames) {
      SwitchToFrameSnippet snippet = new SwitchToFrameSnippet();
      setPreviousResult(frame);
      snippet.runSnippet();
      OverlayExistsSnippet overSnippet = new OverlayExistsSnippet();
      overSnippet.execute();
      CloseOverlaySnippet closeSnippet = new CloseOverlaySnippet();
      closeSnippet.execute();
      GetElementSnippet getElementSnippet = new GetElementSnippet();
      getElementSnippet.execute();
    }
    this.suggestionSnippetResult = SuggestionSnippetResult.Success;
  }
}
