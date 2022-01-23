package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NaturaltextActionExampleDTO {
  Long naturalTextActionId;
  String description;
  String example;
  String workspace;
  String data;
  private Timestamp createdDate;
  private Timestamp updatedDate;

}
