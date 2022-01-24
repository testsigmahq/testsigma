package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KibbutzPluginStepDetails {
  private Map<String, KibbutzElementData> elements;
  private Map<String, KibbutzTestStepTestData> testData;
}
