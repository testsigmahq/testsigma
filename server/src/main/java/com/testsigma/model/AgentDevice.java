/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/

package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.dto.AgentBrowserDTO;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agent_devices")
@Data
@EqualsAndHashCode
@ToString
@Log4j2
public class AgentDevice implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "unique_id")
  private String uniqueId;

  @Column(name = "agent_id")
  private Long agentId;

  @Column(name = "product_model")
  private String productModel;

  @Column(name = "os_version")
  private String osVersion;

  @Column(name = "os_name")
  @Enumerated(value = EnumType.STRING)
  private MobileOs osName;

  @Column(name = "api_level")
  private String apiLevel;

  @Column(name = "abi")
  private String abi;

  @Column(name = "browser_list", columnDefinition = "TEXT")
  @Lob
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private String browserList;

  @Column(name = "is_emulator")
  private Boolean isEmulator;

  @Column(name = "is_online")
  private Boolean isOnline;

  @Column(name = "screen_width")
  private Integer screenWidth;

  @Column(name = "screen_height")
  private Integer screenHeight;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "agent_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Agent agent;

  public String getPlatformOsVersion() {
    String osVersionStr = osVersion;
    if (osVersionStr != null) {
      String[] tokens = osVersionStr.split("\\.");
      osVersionStr = tokens[0] + ".0";
    }
    return osVersionStr;
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

}
