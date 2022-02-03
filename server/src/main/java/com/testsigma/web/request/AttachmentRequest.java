/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AttachmentRequest {
  @NotNull Long entityId;
  @NotEmpty String name;
  @NotNull MultipartFile fileContent;
  @NotNull
  private String entity;
  private String description;
}
