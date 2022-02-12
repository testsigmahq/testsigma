package com.testsigma.automator.runners;

import com.testsigma.automator.constants.DriverSessionType;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.automator.entity.EnvironmentRunResult;
import com.testsigma.automator.entity.TestSuiteEntity;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.Timestamp;
import java.util.*;

@Log4j2
public class ExecutionEnvironmentRunner extends EnvironmentRunner {
  static private final Map<String, Map<Long, List<String>>> lastAccessedUrls = new HashMap<>();
  private TestsuiteRunner testsuiteRunner;

  public ExecutionEnvironmentRunner(TestDeviceEntity testDeviceEntity, EnvironmentRunResult environmentRunResult,
                                    HttpClient webAppHttpClient, HttpClient assetsHttpClient) {
    super(testDeviceEntity, environmentRunResult, webAppHttpClient, assetsHttpClient);
  }

  public static void addUrl(String testPlanId, Long testcaseId, String url) {
    try {
      Map<Long, List<String>> list = ObjectUtils.defaultIfNull(lastAccessedUrls.get(testPlanId), new HashMap<>());
      List<String> urls = ObjectUtils.defaultIfNull(list.get(testcaseId), new ArrayList<>());
      if (!urls.contains(url)) {
        urls.add(url);
      }

      if (!list.containsKey(testcaseId)) {
        list.put(testcaseId, urls);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static Set<Long> getDependenciesByUrl(String testPlanId, String url) {
    Set<Long> testcases = new HashSet<>();
    if (lastAccessedUrls.containsKey(testPlanId)) {
      Map<Long, List<String>> urls = ObjectUtils.defaultIfNull(lastAccessedUrls.get(testPlanId), new HashMap<>());
      for (Map.Entry<Long, List<String>> laUrl : urls.entrySet()) {
        if (laUrl.getValue().contains(url)) {
          testcases.add(laUrl.getKey());
        }
      }
    }
    return testcases;
  }

  public void execute() throws AutomatorException {
    this.testsuiteRunner = new TestSuiteRunnerFactory().getRunner();
    if (!testDeviceEntity.getCreateSessionAtCaseLevel()) {
      testsuiteRunner.startSession(environmentRunResult.getId(), DriverSessionType.ENVIRONMENT_SESSION);
      environmentRunResult.setDeviceAllocatedOn(new Timestamp(System.currentTimeMillis()));
    }
    environmentRunResult = testsuiteRunner.runSuites(testDeviceEntity.getTestSuites());
  }

  public void afterExecute() throws AutomatorException {
    super.afterExecute();
    if (!testDeviceEntity.getCreateSessionAtCaseLevel()) {
      testsuiteRunner.endSession();
    }
    lastAccessedUrls.remove(testPlanId);
  }

  public String getTestPlanId() {
    return String.format("%s-%s", environmentRunResult.getId(), testDeviceEntity.getId());
  }

  public void checkForEmptyEnvironment() throws AutomatorException {
    boolean isEmpty = true;
    for (TestSuiteEntity entity : testDeviceEntity.getTestSuites()) {
      if (entity.getTestCases().size() > 0) {
        isEmpty = false;
        break;
      }
    }

    if (isEmpty) {
      AutomatorException ex = new AutomatorException(AutomatorMessages.NO_TESTCASES_AVAILABLE,
        AutomatorMessages.NO_TESTCASES_AVAILABLE);
      ex.setDispMessage(AutomatorMessages.NO_TESTCASES_AVAILABLE);
      throw ex;
    }
  }
}
