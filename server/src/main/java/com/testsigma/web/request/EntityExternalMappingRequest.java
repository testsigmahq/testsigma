/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.testsigma.model.EntityType;
import lombok.Data;

import java.util.Map;

@Data
public class EntityExternalMappingRequest {

  Long id;
  EntityType entityType;
  Long entityId;
  Long applicationId;
  Map<String, Object> fields;
  Boolean linkToExisting;
  String externalId;
  String message;
  Boolean assetsPushFailed;
}
