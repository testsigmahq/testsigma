package com.testsigma.automator.runners;

import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.webservices.WebserviceUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class RestTestcaseStepRunner extends TestcaseStepRunner {

  public RestTestcaseStepRunner(WorkspaceType workspaceType, Platform os) {
    super(workspaceType, os);
  }

  @Override
  protected void execute(Map<String, String> envSetting, TestCaseStepResult result, TestCaseStepEntity testcaseStep,
                         TestCaseResult testCaseResult) throws AutomatorException {
    log.info("Executing REST step, step:" + testcaseStep);
    if (breakOrContinueLoopStep(testcaseStep, result)) {
      log.info("Its a break or continue step, not executing REST API call");
      return;
    }
    new WebserviceUtil().execute(testcaseStep, result,  envSetting, testCaseResult);
  }

  private boolean breakOrContinueLoopStep(TestCaseStepEntity testcaseStep, TestCaseStepResult result) {
    log.info("Validating for Break or Continue step");
    TestStepType stepType = testcaseStep.getType();
    if (stepType != null && (stepType == TestStepType.BREAK_LOOP || stepType == TestStepType.CONTINUE_LOOP)) {
      result.setResult(ResultConstant.SUCCESS);
      return true;
    }
    return false;
  }

  @Override
  protected void onStepFailure(ExecutionLabType exeType, WorkspaceType workspaceType, TestPlanRunSettingEntity settings)
    throws AutomatorException {
  }
}
