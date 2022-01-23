/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.testsigma.model.UploadType;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

@Data
public class UploadRequest {
  @NotEmpty String name;
  @NotNull UploadType uploadType;
  @Nullable MultipartFile fileContent;
  @NotNull Long workspaceId;
}
