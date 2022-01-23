package com.testsigma.automator.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.automator.deserialize.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

@Data
public class SuggestionEngineResultMetaData {
  private String tagName;
  private String tabCount;
  @JsonSerialize(using = JSONObjectSerializer.class)
  private JSONObject suggestions;

}
