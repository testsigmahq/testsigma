package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TestDataProfileDTO {
  private Long id;
  private String testDataName;
  private String testData;
  private List<TestDataSetDTO> data;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private Long versionId;
}
