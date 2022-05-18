/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.dto.AgentBrowserDTO;
import com.testsigma.dto.PrivateGridBrowserDTO;
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

@Entity
@Table(name = "private_grid_nodes")
@Data
@ToString
@EqualsAndHashCode
@Log4j2
public class PrivateGridNode implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  @Column(name = "grid_url")
  private String gridURL;

  @Column(name = "node_name")
  private String nodeName;


  public List<PrivateGridBrowser> getBrowserList() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
  }

  public void setBrowserList(List<PrivateGridBrowser> browserList) {
    this.browserList = new ObjectMapperService().convertToJson(browserList);
  }

  public List<PrivateGridBrowserDTO> getBrowserListDTO() {
    return browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
  }

  public String getBrowserVersion(String browser) {
    List<PrivateGridBrowserDTO> list = browserList == null ? new ArrayList<>() :
      new ObjectMapperService().parseJson(browserList, new TypeReference<>() {
      });
    return browser;
  }
}
