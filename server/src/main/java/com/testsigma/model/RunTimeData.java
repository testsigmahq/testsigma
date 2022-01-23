/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.testsigma.converter.JSONObjectConverter;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "runtime_data")
@Data
public class RunTimeData {

  @Column(name = "data", columnDefinition = "json")
  protected String data;
  @Column(name = "test_plan_run_id")
  Long testPlanRunId;
  @Column(name = "session_id")
  String sessionId;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  public JSONObject getData() {
    return JSONObjectConverter.toJsonObject(data);
  }

  public void setData(JSONObject data) {
    this.data = data == null ? null : data.toString();
  }
}
