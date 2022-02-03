package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Data
@Log4j2
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecorderDependentDataDTO {
  private String name;
  private Map<String, String> attributes;
  private boolean viewMore;

  public void setAttributes(String attributesString) {
    if (attributesString != null) {
      this.attributes = new ObjectMapperService().parseJson(attributesString, new TypeReference<>() {
      });
    } else {
      this.attributes = null;
    }
  }
}
