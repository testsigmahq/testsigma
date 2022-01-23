/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.AgentOs;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonListRootName(name = "agents")
@JsonRootName(value = "agent")
public class AgentXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("unique-id")
  private String uniqueId;
  @JsonProperty("agency-version")
  private String agentVersion;
  @JsonProperty("browser-list")
  private String browserList;
  @JsonProperty("created-by")
  private Long createdBy;
  @JsonProperty("updated-by")
  private Long updatedBy;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("created-date")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("updated-date")
  private Timestamp updatedDate;
  @JsonProperty("host-name")
  private String hostName;
  @JsonProperty("title")
  private String title;
  @JsonProperty("ip-address")
  private String ipAddress;
  @JsonProperty("os-type")
  private AgentOs osType;
  @JsonProperty("os-version")
  private String osVersion;

  public List<AgentBrowserXMLDTO> getBrowserList() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
  }

  public void setBrowserList(List<AgentBrowserXMLDTO> browserList) {
    this.browserList = new ObjectMapperService().convertToJson(browserList);
  }

  public List<AgentBrowserXMLDTO> getBrowserListDTO() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
  }

  public String getBrowserVersion(String browser) {
    List<AgentBrowserXMLDTO> list = browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
    for (AgentBrowserXMLDTO browserDTO : list) {
      if (browserDTO.getName().getBrowserName().equals(browser)) {
        return ((float) browserDTO.getMajorVersion()) + "";
      }
    }
    return browser;
  }
}
