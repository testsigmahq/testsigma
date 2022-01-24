package com.testsigma.automator.runners;

import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.entity.ConditionType;
import com.testsigma.automator.entity.ResultConstant;
import com.testsigma.automator.entity.TestCaseStepEntity;
import com.testsigma.automator.entity.TestCaseStepResult;

public class RunnerUtil {

  public boolean canSkipIfElse(TestCaseStepResult parentResult, TestCaseStepEntity testcaseStep,
                               TestCaseStepResult stepResult) {

    if (parentResult != null && parentResult.getConditionType() != null &&
      parentResult.getConditionType() == ConditionType.CONDITION_IF
      && testcaseStep.getConditionType() != null &&
      testcaseStep.getConditionType() == ConditionType.CONDITION_ELSE
      && parentResult.getIsConditionSuccess()) {
      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_CONDITION_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipIfElseIf(TestCaseStepResult parentResult,
                                 TestCaseStepEntity testcaseStep,
                                 TestCaseStepResult stepResult) {

    if (parentResult != null && parentResult.getConditionType() != null &&
      parentResult.getConditionType() == ConditionType.CONDITION_IF
      && testcaseStep.getConditionType() != null &&
      testcaseStep.getConditionType() == ConditionType.CONDITION_ELSE_IF
      && parentResult.getIsConditionSuccess()) {
      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_CONDITION_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipElseIfElseIf(TestCaseStepResult parentResult,
                                     TestCaseStepEntity testcaseStep,
                                     TestCaseStepResult stepResult) {
    if (parentResult != null && parentResult.getConditionType() != null &&
      parentResult.getConditionType() == ConditionType.CONDITION_ELSE_IF
      && testcaseStep.getConditionType() != null &&
      testcaseStep.getConditionType() == ConditionType.CONDITION_ELSE_IF
      && parentResult.getIsConditionSuccess()) {
      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_CONDITION_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipElseIfElse(TestCaseStepResult parentResult,
                                   TestCaseStepEntity testcaseStep,
                                   TestCaseStepResult stepResult) {
    if (parentResult != null && parentResult.getConditionType() != null &&
      parentResult.getConditionType() == ConditionType.CONDITION_ELSE_IF
      && testcaseStep.getConditionType() != null &&
      testcaseStep.getConditionType() == ConditionType.CONDITION_ELSE
      && parentResult.getIsConditionSuccess()) {
      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_CONDITION_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipNormalStep(TestCaseStepResult parentResult,
                                   TestCaseStepEntity testcaseStep,
                                   TestCaseStepResult stepResult) {
    if ((testcaseStep.getParentId() != null && testcaseStep.getParentId() > 0 &&
      (parentResult == null || (parentResult != null && !parentResult.getIsConditionSuccess()))
      && isNOtConditionStep(testcaseStep))) {
      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_CONDITION_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipForLoop(TestCaseStepResult parentResult,
                                TestCaseStepEntity testcaseStep,
                                TestCaseStepResult stepResult) {
    if (testcaseStep.getParentId() != null && testcaseStep.getParentId() > 0
      && (parentResult == null || (parentResult != null && !parentResult.getIsConditionSuccess())
      && testcaseStep.getConditionType() == ConditionType.LOOP_FOR)) {

      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipIfCondition(TestCaseStepResult parentResult,
                                    TestCaseStepEntity testcaseStep,
                                    TestCaseStepResult stepResult) {
    if (testcaseStep.getParentId() != null && testcaseStep.getParentId() > 0
      && (parentResult == null || (parentResult != null && !parentResult.getIsConditionSuccess())
      && testcaseStep.getConditionType() == ConditionType.CONDITION_IF)) {

      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_CONDITION_FAILED);
      return true;
    }
    return false;
  }

  private boolean isNOtConditionStep(TestCaseStepEntity testcaseStep) {
    return (testcaseStep.getConditionType() == null ||
      testcaseStep.getConditionType() == ConditionType.NOT_USED);
  }

  public boolean nestedConditionalStep(TestCaseStepResult parentResult,
                                       TestCaseStepEntity testcaseStep,
                                       TestCaseStepResult stepResult) {
    if ((testcaseStep.getParentId() != null && testcaseStep.getParentId() > 0 &&
      parentResult != null && ResultConstant.NOT_EXECUTED.equals(parentResult.getResult()))) {

      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_FAILED);
      return true;
    }
    return false;
  }

  public boolean canSkipForLoopTopSteps(boolean isStepGroup, TestCaseStepResult parentResult,
                                        TestCaseStepEntity parentStep, TestCaseStepEntity testcaseStep,
                                        TestCaseStepResult stepResult) {
    if (!isStepGroup && (testcaseStep.getParentId() != null && testcaseStep.getParentId() > 0 &&
      parentStep.getId().equals(testcaseStep.getParentId()) && (parentResult != null
      && parentResult.getResult().equals(ResultConstant.NOT_EXECUTED)))) {

      stepResult.setSkipExe(true);
      stepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PARENT_FAILED);
      return true;
    }
    return false;
  }

  public boolean isLoopSteps(boolean isStepGroup, TestCaseStepEntity parentStep, TestCaseStepEntity testcaseStep) {
    return (!isStepGroup && (testcaseStep.getParentId() != null && testcaseStep.getParentId() > 0 &&
      parentStep.getId().equals(testcaseStep.getParentId())));
  }
}
