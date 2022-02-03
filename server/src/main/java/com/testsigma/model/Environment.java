/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.converter.EnvironmentDataConverter;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "environments", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
@Log4j2
@EqualsAndHashCode
public class Environment implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;


  @Column(length = 250, nullable = false)
  @NotNull
  private String name;


  @Column(name = "description")
  private String description;


  @Column(name = "parameters", columnDefinition = "text")
  private String parameters;



  @OneToMany(fetch = FetchType.LAZY, mappedBy = "environment")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AbstractTestPlan> testPlans;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "environment")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestPlanResult> testPlanResults;

  public JSONObject getParameters() {
    return new JSONObject(parameters);
  }

  public void setParameters(JSONObject params) {
    this.parameters = params.toString();
  }

  public Map<String, String> getData() {
    if (this.parameters != null)
      return new ObjectMapperService().parseJson(this.parameters, LinkedHashMap.class);
    return new LinkedHashMap();
  }

  public void setData(Map<String, String> data) {
    this.parameters = new EnvironmentDataConverter().convertToDatabaseColumn(data);
  }

}
