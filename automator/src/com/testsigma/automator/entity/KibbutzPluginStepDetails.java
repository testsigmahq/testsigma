package com.testsigma.automator.entity;

import lombok.Data;

import java.util.Map;

@Data
public class KibbutzPluginStepDetails {
  private Map<String, KibbutzElementData> elements;
  private Map<String, KibbutzTestStepTestData> testData;
}
