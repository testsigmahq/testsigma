/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import lombok.Data;

@Data
public class ImportNotificationDTO {
  Integer failedCount;
  Integer totalCount;
  String url;
  Boolean success;
}
