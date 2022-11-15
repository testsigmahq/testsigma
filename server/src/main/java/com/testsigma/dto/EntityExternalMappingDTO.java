/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.EntityType;
import lombok.Data;

import java.util.Map;

@Data
public class EntityExternalMappingDTO {

  Long id;
  EntityType type;
  Long entityId;
  Long workspaceId;
  String externalId;
  Map<String, Object> fields;
  Boolean pushFailed;
  String message;
}
