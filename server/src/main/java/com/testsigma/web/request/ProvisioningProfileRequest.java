/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProvisioningProfileRequest {
  private Long id;
  private String name;
  private String teamId;
  private MultipartFile cer;
  private MultipartFile provisioningProfile;
}
