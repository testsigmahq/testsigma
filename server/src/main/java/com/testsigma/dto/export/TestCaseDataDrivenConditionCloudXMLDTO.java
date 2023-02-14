package com.testsigma.dto.export;

import com.testsigma.model.DataDrivenIterationType;
import com.testsigma.model.Operator;
import lombok.Data;

@Data
public class TestCaseDataDrivenConditionCloudXMLDTO {
  private Long id;
  private Long testCaseId;
  private DataDrivenIterationType iterationType;
  private Operator operator;
  private Long testDataProfileId;
  private Long testDataIndex;
  private Long testDataEndIndex;
}
