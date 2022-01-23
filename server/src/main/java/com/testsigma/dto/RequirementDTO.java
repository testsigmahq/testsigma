package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
public class RequirementDTO {

  private Long id;

  private String requirementName;

  private String requirementDescription;

  private Map<String, String> files;

  private Long workspaceVersionId;

  private Timestamp createdDate;

  private Timestamp updatedDate;

  private WorkspaceVersionDTO version;

}
