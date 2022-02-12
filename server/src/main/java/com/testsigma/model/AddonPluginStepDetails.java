package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddonPluginStepDetails {
  private Map<String, AddonElementData> elements;
  private Map<String, AddonTestStepTestData> testData;
}
