package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.ElementCreateType;
import com.testsigma.model.LocatorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode
public class APIElementDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("workspace_version_id")
  private Long workspaceVersionId;
  @JsonProperty("locator_value")
  private String locatorValue;
  @JsonProperty("name")
  private String name;
  @JsonProperty("type")
  private Integer type;
  @JsonProperty("created_type")
  private ElementCreateType createdType;
  @JsonProperty("locator_type")
  private LocatorType locatorType;
  @JsonProperty("attributes")
  private String attributes;
  @JsonProperty("is_dynamic")
  private Boolean isDynamic;
  @JsonProperty("created_date")
  private Timestamp createdDate;
  @JsonProperty("updated_date")
  private Timestamp updatedDate;
  @JsonProperty("screen_name_id")
  private Long screenNameId;
  @JsonProperty("screen_name_obj")
  private APIElementScreenNameDTO screenNameObj;
}
