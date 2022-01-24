package com.testsigma.automator.runners;

import com.testsigma.automator.entity.WorkspaceType;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.entity.TestStepType;


public class TestcaseStepRunnerFactory {

  public TestcaseStepRunner getRunner(WorkspaceType workspaceType,
                                      Platform os, TestStepType stepType) {

    if (workspaceType == WorkspaceType.Rest) {
      return new RestTestcaseStepRunner(workspaceType, os);
    }
    if ((stepType == TestStepType.REST_STEP)) {
      return new RestTestcaseStepRunner(workspaceType, os);
    }
    return new WebTestcaseStepRunner(workspaceType, os);
  }
}
