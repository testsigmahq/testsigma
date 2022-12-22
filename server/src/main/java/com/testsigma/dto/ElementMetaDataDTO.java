package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectDeserializer;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementMetaDataDTO {
  @JsonProperty("xPath")
  private String xPath;
  @JsonProperty("parents")
  private List<RecorderDependentDataDTO> parents;
  @JsonProperty("current-element")
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject currentElement;
  @JsonProperty("following-sibling")
  private List<RecorderDependentDataDTO> followingSiblings;
  @JsonProperty("preceding-sibling")
  private List<RecorderDependentDataDTO> precedingSiblings;
  @JsonProperty("childs_first_level")
  private List<RecorderDependentDataDTO> firstLevelChildren;
  @JsonProperty("childs_second_level")
  private List<RecorderDependentDataDTO> secondLevelChildren;
  @JsonProperty("testdata")
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject testData;

  public void setCurrentElement(String currentElementString) {
    this.currentElement = new JSONObject(currentElementString);
  }
}
