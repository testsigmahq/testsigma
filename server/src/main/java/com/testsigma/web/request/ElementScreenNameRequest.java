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

import java.io.Serializable;

@Data

public class ElementScreenNameRequest implements Serializable {
  private Long id;
  private Long workspaceVersionId;
  private String name;
}
