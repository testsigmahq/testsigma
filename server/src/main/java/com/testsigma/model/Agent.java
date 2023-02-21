/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.dto.AgentBrowserDTO;
import com.testsigma.security.api.APIToken;
import com.testsigma.service.JWTTokenService;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "agents")
@Data
@ToString
@EqualsAndHashCode
@Log4j2
public class Agent implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "unique_id")
  private String uniqueId;

  @Column(name = "agent_version")
  private String agentVersion;

  @Column(name = "browser_list", columnDefinition = "TEXT")
  @Lob
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private String browserList;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<AgentDevice> agentDevices;

  @Column(name = "host_name")
  private String hostName;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "os_type")
  @Enumerated(EnumType.STRING)
  private AgentOs osType;

  @Column(name = "title")
  private String title;

  @Column(name = "os_version")
  private String osVersion;

  @Column(name = "imported_id")
  private Long importedId;

  public String generateJwtApiKey(String serverUuid) {
    AgentType agentType = AgentType.HYBRID;
    return JWTTokenService.generateToken(new APIToken(uniqueId,
      agentType, serverUuid));
  }

  public List<AgentBrowser> getBrowserList() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
  }

  public void setBrowserList(List<AgentBrowser> browserList) {
    this.browserList = new ObjectMapperService().convertToJson(browserList);
  }

  public List<AgentBrowserDTO> getBrowserListDTO() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
  }

  public String getBrowserVersion(String browser) {
    List<AgentBrowserDTO> list = browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
    for (AgentBrowserDTO browserDTO : list) {
      if (browserDTO.getName().getBrowserName().equals(browser)) {
        return ((float) browserDTO.getMajorVersion()) + "";
      }
    }
    return browser;
  }


  public String getPlatformOsVersion(Platform platform) {
    if (Platform.Linux.equals(platform)) {
      return Platform.Linux.getOs();
    }
    String osVersionStr = osVersion;
    if (osVersionStr != null) {
      String[] tokens = osVersionStr.split("\\.");
      if (tokens.length >= 2) {
        osVersionStr = tokens[0] + "." + tokens[1];
      }
    }
    osVersionStr = platform.getVersionPrefix() + " " + osVersionStr;
    if (osVersionStr.startsWith("macOS 11."))
      osVersionStr = "macOS 11.0";
    if(osVersionStr.startsWith("macOS 12."))
      osVersionStr = "macOS 12.0";
    if(osVersionStr.startsWith("macOS 13."))
      osVersionStr = "Ventura";
    osVersionStr = osVersionStr.trim();
    return osVersionStr;
  }
}
