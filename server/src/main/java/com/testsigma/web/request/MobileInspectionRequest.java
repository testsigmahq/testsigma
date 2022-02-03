package com.testsigma.web.request;

import com.testsigma.model.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class MobileInspectionRequest {
  private Long id;
  private Platform platform;
  private Long agentDeviceId;
  private MobileInspectionStatus status;
  private TestPlanLabType labType;
  private Long platformDeviceId;
  private String appActivity;
  private String bundleId;
  private Long appUploadId;
  private String sessionId;
  private String applicationPackage;
  private AppPathType applicationPathType;
  private Timestamp startedAt;
  private Timestamp finishedAt;
  private Timestamp lastActiveAt;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private List<Capability> capabilities;
}

