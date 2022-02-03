package com.testsigma.step.processors;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;

@Data
public class TestDataSet implements Serializable {

  private String name;
  private String description;
  private Boolean expectedToFail = false;
  private LinkedHashMap<String, String> data;

  public TestDataSet(String name, String description, Boolean expectedToFail, LinkedHashMap<String, String> data) {
    this.name = name;
    this.description = description;
    this.expectedToFail = expectedToFail;
    this.data = data;
  }
}
