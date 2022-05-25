package com.testsigma.automator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestDataProfileEntity {
  private Integer testDataIndex;
  private String testDataSetName;
  private String testDataProfile;
}
