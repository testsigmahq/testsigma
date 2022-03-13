package com.testsigma.automator.entity;

import lombok.Data;

import java.util.Map;

@Data
public class AddonPluginStepDetails {
  private Map<String, AddonElementData> elements;
  private Map<String, AddonTestStepTestData> testData;
}
