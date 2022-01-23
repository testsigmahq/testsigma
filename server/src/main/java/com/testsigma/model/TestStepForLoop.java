package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Embeddable;


@Data
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepForLoop {
  private Integer startIndex;
  private Integer endIndex;
  private Long testDataId;
}
