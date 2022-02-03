/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.testsigma.converter.JSONObjectConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "rest_step_details")
@Data
public class RestStep {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "step_id")
  private Long stepId;


  @Column
  private String url;

  @Column
  @Enumerated(EnumType.STRING)
  private HttpRequestMethod method;


  @Column(name = "request_headers")
  private String requestHeaders;


  @Column
  private String payload;


  @Column(name = "status")
  private String expectedResponseStatus;

  @Column(name = "expected_result_type")
  private String expectedResultType;

  @Column(name = "response_compare_type")
  @Enumerated(EnumType.STRING)
  private RestStepCompareType responseCompareType;

  @Column(name = "header_compare_type")
  @Enumerated(EnumType.STRING)
  private RestStepCompareType headerCompareType = RestStepCompareType.LENIENT;

  @Column(name = "response_headers")
  private String responseHeaders;

  @Column(name = "store_metadata")
  private Boolean storeMetadata;


  @Column
  private String response;


  @Column(name = "header_runtime_data")
  private String headerRuntimeData;


  @Column(name = "body_runtime_data")
  private String bodyRuntimeData;

  @Column(name = "follow_redirects")
  private Boolean followRedirects;


  @Column(name = "authorization_type")
  @Enumerated(EnumType.STRING)
  private RestStepAuthorizationType authorizationType;

  @Column(name = "authorization_value")
  private String authorizationValue;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @OneToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "step_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestStep testStep;
  @Transient
  private Boolean isMultipart;

  public JSONObject getAuthorizationValue() {
    return new JSONObjectConverter().convertToEntityAttribute(this.authorizationValue);
  }

  public void setAuthorizationValue(JSONObject authorizationValue) {
    this.authorizationValue = new JSONObjectConverter().convertToDatabaseColumn(authorizationValue);
  }

  public JSONObject getRequestHeaders() {
    return new JSONObjectConverter().convertToEntityAttribute(this.requestHeaders);
  }

  public void setRequestHeaders(JSONObject requestHeaders) {
    this.requestHeaders = new JSONObjectConverter().convertToDatabaseColumn(requestHeaders);
  }

  public JSONObject getResponseHeaders() {
    return new JSONObjectConverter().convertToEntityAttribute(this.responseHeaders);
  }

  public void setResponseHeaders(JSONObject responseHeaders) {
    this.responseHeaders = new JSONObjectConverter().convertToDatabaseColumn(responseHeaders);
  }

  public JSONObject getHeaderRuntimeData() {
    return new JSONObjectConverter().convertToEntityAttribute(this.headerRuntimeData);
  }

  public void setHeaderRuntimeData(JSONObject headerRuntimeData) {
    this.headerRuntimeData = new JSONObjectConverter().convertToDatabaseColumn(headerRuntimeData);
  }

  public JSONObject getBodyRuntimeData() {
    return new JSONObjectConverter().convertToEntityAttribute(this.bodyRuntimeData);
  }

  public void setBodyRuntimeData(JSONObject bodyRuntimeData) {
    this.bodyRuntimeData = new JSONObjectConverter().convertToDatabaseColumn(bodyRuntimeData);
  }
}
