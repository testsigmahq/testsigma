package com.testsigma.dto.export;

import lombok.Data;

@Data
public class TestCaseDataDrivenConditionCloudXMLDTO {
  private Long id;
  private Long testCaseId;
  private Long testDataProfileId;
  private Long testDataIndex;
  private Long testDataEndIndex;
}
