package com.testsigma.web.request;

import com.testsigma.dto.WorkspaceVersionDTO;
import lombok.Data;

@Data
public class RequirementRequest {

  private String requirementName;

  private String requirementDescription;

  private Long workspaceVersionId;

  private WorkspaceVersionDTO version;

}
