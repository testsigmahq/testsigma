package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepCloudForLoop {
  @JsonProperty("loop_start")
  private int forLoopStartIndex;
  @JsonProperty("loop_end")
  private int forLoopEndIndex;
  @JsonProperty("test_data_id")
  private Long forLoopTestDataId;
}
