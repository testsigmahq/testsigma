/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.UploadStatus;
import com.testsigma.model.UploadType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UploadVersionDTO {
  Long id;
  Timestamp createdDate;
  Timestamp updatedDate;
  String name;
  String path;
  String fileName;
  UploadType type;
  String version;
  Long fileSize;
  String preSignedURL;
  Boolean signed = Boolean.FALSE;
  UploadStatus uploadStatus;
  Long uploadId;
}
