/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UploadDTO {
  Long id;
  Timestamp createdDate;
  Timestamp updatedDate;
  String name;
  Long latestVersionId;
}
