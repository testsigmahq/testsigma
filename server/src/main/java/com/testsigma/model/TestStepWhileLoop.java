package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class TestStepWhileLoop {

  @JsonProperty("test_data_id")
  @Column(name = "while_loop_test_data_id")
  private Long testDataId;

  public Long getTestDataId() {
    if (testDataId == null)
      return 0L;
    return testDataId;

  }
}
