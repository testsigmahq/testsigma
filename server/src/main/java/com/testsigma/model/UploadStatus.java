package com.testsigma.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UploadStatus {
  NOT_USED("Not Used"),
  Completed("Completed"),
  InProgress("In Progress"),
  Failed("Failed");

  private String name;
}
