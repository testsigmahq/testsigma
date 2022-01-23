package com.testsigma.web.request;

import lombok.Data;

@Data
public class TestStepForLoopRequest {
  private int startIndex;
  private int endIndex;
  private Long testDataId;
}
