/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class PrivateGridNodeDTO {
  private Long id;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private List<PrivateGridBrowserDTO> browserList = new ArrayList<>();
  private String nodeName;
  private String gridURL;
}
