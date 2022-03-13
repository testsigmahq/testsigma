package com.testsigma.web.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestDataProfileRequest {

  Long id;
  private String testDataName;
  private String testData;
  private Map<String, String> renamedColumns;
  private List<TestDataSetRequest> data;
  private Long versionId;
}
