package com.testsigma.automator.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.automator.constants.ElementCreateType;
import com.testsigma.automator.deserialize.JSONObjectDeserialize;
import com.testsigma.automator.deserialize.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

@Data
public class ElementEntity {
  private Long id;
  private Long workspaceVersionId;
  private String locatorValue;
  private String name;
  private Integer type;
  private ElementCreateType createdType;
  private LocatorType locatorType;
  private String screenName;
  private Boolean isAdvanced = false;
  @JsonDeserialize(using = JSONObjectDeserialize.class)
  @JsonSerialize(using = JSONObjectSerializer.class)
  private JSONObject metadata;
  private String attributes;
  private Boolean isDynamic = false;


}
