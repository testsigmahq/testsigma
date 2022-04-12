/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.UploadStatus;
import com.testsigma.model.UploadType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class APIUploadDTO {
  Long id;
  @JsonProperty("created_date")
  Timestamp createdDate;
  @JsonProperty("updated_date")
  Timestamp updatedDate;
  @JsonProperty("name")
  String name;
  @JsonProperty("latest_version_id")
  Long latestVersionId;

}
