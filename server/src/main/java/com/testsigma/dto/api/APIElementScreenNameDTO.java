package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class APIElementScreenNameDTO {
  private Long id;
  @JsonProperty("workspace_version_id")
  private Long workspaceVersionId;
  private String name;
}
