package com.testsigma.automator.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DatabaseStepEntity implements Serializable {

  /**
   *
   */
  private Long id;
  private String url;
  private Integer type;
  private String hostname;
  private String databaseName;
  private Integer port;
  private String username;
  private String password;
  private String query;
  private String compareType;
  private String response;
  private Long stepId;
  private boolean storeMetadata;
}
