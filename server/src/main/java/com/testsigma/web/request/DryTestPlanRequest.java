package com.testsigma.web.request;

import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.TestPlanType;
import com.testsigma.model.Screenshot;
import lombok.Data;

import java.util.List;

@Data
public class DryTestPlanRequest {
  private Long id;
  private Long workspaceVersionId;
  private Long testCaseId;
  private Integer elementTimeOut;
  private Integer pageTimeOut;
  private Long environmentId;
  private Screenshot screenshot;
  private TestPlanLabType testPlanLabType;
  private boolean visualTestingEnabled;
  private TestPlanType testPlanType = TestPlanType.CROSS_BROWSER;
  private Boolean matchBrowserVersion = false;
  private List<TestDeviceRequest> testDevices;
}
