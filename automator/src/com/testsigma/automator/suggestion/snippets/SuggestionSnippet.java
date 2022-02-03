package com.testsigma.automator.suggestion.snippets;

import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.SuggestionEngineResult;
import com.testsigma.automator.entity.TestCaseStepEntity;
import com.testsigma.automator.actions.ElementAction;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.RemoteWebDriver;

@Log4j2
@Data
public abstract class SuggestionSnippet extends ElementAction {
  public static Object previousResult = new Object();
  public static TestCaseStepEntity testCaseStepEntity;
  protected final String LOOP_STEPS = "loopsteps";
  protected final String IF_STEPS = "ifsteps";
  public SuggestionSnippetResult suggestionSnippetResult;
  public SuggestionEngineResult engineResult;
  protected RemoteWebDriver driver = DriverManager.getDriverManager().getDriver().getRemoteWebDriver();

  public static Object getPreviousResult() {
    return previousResult;
  }

  public static void setPreviousResult(Object previousResult) {
    SuggestionSnippet.previousResult = previousResult;
  }

  public static TestCaseStepEntity getTestCaseStepEntity() {
    return testCaseStepEntity;
  }

  public static void setTestCaseStepEntity(TestCaseStepEntity testCaseStepEntity) {
    SuggestionSnippet.testCaseStepEntity = testCaseStepEntity;
  }

  @Override
  protected void execute() throws Exception {
  }

  @Override
  protected void handleException(Exception e) {
    engineResult.setResult(SuggestionSnippetResult.Failure);
  }
}
