package com.testsigma.dto;

import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.MobileInspectionStatus;
import com.testsigma.model.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@EqualsAndHashCode
public class MobileInspectionDTO {
  private Long id;
  private Platform platform;
  private Long agentDeviceId;
  private MobileInspectionStatus status;
  private TestPlanLabType labType;
  private Long platformDeviceId;
  private String appActivity;
  private Long appUploadId;
  private String sessionId;
  private Timestamp startedAt;
  private Timestamp finishedAt;
  private Timestamp lastActiveAt;
  private Timestamp createdDate;
  private Timestamp updatedDate;
}
