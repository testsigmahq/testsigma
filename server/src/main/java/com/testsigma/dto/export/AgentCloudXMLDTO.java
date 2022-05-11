/******************************************************************************
 * Copyright (C) 2019 Testsigma Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonListRootName(name = "Agents")
@JsonRootName(value = "Agent")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("uniqueId")
  private String uniqueId;
  @JsonProperty("SystemId")
  private Long systemId;
  @JsonProperty("AgencyVersion")
  private String agentVersion;
  @JsonProperty("BrowserList")
  private String browserList;
  @JsonProperty("CreatedBy")
  private Long createdBy;
  @JsonProperty("UpdatedBy")
  private Long updatedBy;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("CreatedDate")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("UpdatedDate")
  private Timestamp updatedDate;
  @JsonProperty("Enabled")
  private Boolean enabled;
  @JsonProperty("HostName")
  private String hostName;
  @JsonProperty("VisibleToAll")
  private Boolean visibleToAll;
  @JsonProperty("MobileEnabled")
  private Boolean mobileEnabled;
  @JsonProperty("UpgradeAgentJar")
  private Boolean upgradeAgentJar = false;
  @JsonProperty("UpgradeJre")
  private Boolean upgradeJre = false;
  @JsonProperty("UpgradeAndroidTools")
  private Boolean upgradeAndroidTools = false;
  @JsonProperty("UpgradeIosTools")
  private Boolean upgradeIosTools = false;
  @JsonProperty("UpgradeAppium")
  private Boolean upgradeAppium = false;

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
