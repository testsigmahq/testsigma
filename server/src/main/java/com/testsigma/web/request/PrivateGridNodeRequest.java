/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.model.PrivateGridBrowser;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
public class PrivateGridNodeRequest {
  private Long id;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private List<PrivateGridBrowserRequest> browserList;
  private String nodeName;
  private String gridURL;

  public List<PrivateGridBrowser> getNodeBrowserList() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(new ObjectMapperService().convertToJson(browserList), new TypeReference<>() {
      });
  }
}
