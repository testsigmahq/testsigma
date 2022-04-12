package com.testsigma.agent.request;

import com.testsigma.automator.entity.ExecutionLabType;
import com.testsigma.automator.entity.MobileInspectionStatus;
import com.testsigma.automator.entity.Platform;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MobileInspectionRequest {
  private Long id;
  private Platform platform;
  private Long agentDeviceId;
  private MobileInspectionStatus status;
  private ExecutionLabType executionLabType;
  private Long platformDeviceId;
  private String appActivity;
  private Long uploadVersionId;
  private String sessionId;
  private Timestamp startedAt;
  private Timestamp finishedAt;
  private Timestamp lastActiveAt;
  private Timestamp createdDate;
  private Timestamp updatedDate;
}
