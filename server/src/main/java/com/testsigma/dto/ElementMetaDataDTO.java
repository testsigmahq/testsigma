package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
  private String xPath;
  private List<RecorderDependentDataDTO> parents;
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject currentElement;
  private List<RecorderDependentDataDTO> followingSiblings;
  private List<RecorderDependentDataDTO> precedingSiblings;
  private List<RecorderDependentDataDTO> firstLevelChildren;
  private List<RecorderDependentDataDTO> secondLevelChildren;
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject testData;

  public void setCurrentElement(String currentElementString) {
    this.currentElement = new JSONObject(currentElementString);
  }
}
