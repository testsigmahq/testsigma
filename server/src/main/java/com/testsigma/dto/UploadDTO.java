/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.SupportedDeviceType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UploadDTO {
  Long id;
  Timestamp createdDate;
  Timestamp updatedDate;
  SupportedDeviceType supportedDeviceType;
  String name;
  Long latestVersionId;
}
