
package com.testsigma.tasks;

import com.testsigma.http.AssetsHttpClient;
import com.testsigma.http.WebAppHttpClient;
import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.executions.AbstractTestPlanRunTask;
import com.testsigma.automator.runners.ExecutionEnvironmentRunner;
import org.apache.logging.log4j.ThreadContext;

public class TestPlanRunTask extends AbstractTestPlanRunTask {

  public TestPlanRunTask(TestDeviceEntity testDeviceEntity) {
    super(testDeviceEntity, ThreadContext.get("X-Request-Id"), new WebAppHttpClient(), new AssetsHttpClient());
  }

  @Override
  public void execute() throws Exception {
    ExecutionEnvironmentRunner driver = new ExecutionEnvironmentRunner(environment, environmentRunResult,
      webHttpClient, assetsHttpClient);
    environmentRunResult = driver.run();
  }

  @Override
  public void afterExecute() throws AutomatorException {
    super.afterExecute();
    AutomatorConfig.getInstance().getAppBridge().postEnvironmentResult(environmentRunResult);
  }
}
