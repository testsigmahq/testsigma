package com.testsigma.dto;

import lombok.Data;

import java.net.URL;
import java.util.Date;

@Data
public class AddonDTO {
  private Long id;
  private Long addonId;
  private Date createdAt;
  private Date updatedAt;
  private URL classesPath;
}
