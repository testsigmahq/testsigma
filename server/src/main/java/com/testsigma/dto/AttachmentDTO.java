/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AttachmentDTO {
  private Long id;
  private Long entityId;
  private String name;
  private String entity;
  private String description;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private String preSignedURL;
}
