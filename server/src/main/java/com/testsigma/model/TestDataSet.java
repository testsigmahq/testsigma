/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.converter.JSONObjectConverter;
import com.testsigma.serializer.JSONObjectDeserializer;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "test_data_sets")
@Data
@ToString
@EqualsAndHashCode
public class TestDataSet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name ="test_data_id")
  private Long testDataProfileId;

  @Column
  private String name;

  @Column
  private String description;

  @Column(name = "expected_to_fail")
  private Boolean expectedToFail = false;

  @Column(name = "position")
  private Long position;

  @Column
  @JsonSerialize(using = JSONObjectSerializer.class)
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private String data;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_data_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  private TestData testData;

  //  @JsonSerialize(using = JSONObjectSerializer.class)
  public JSONObject getData(){
    if(data == null){
      return null;
    }
    return new JSONObject(data);
  }

  public void setData(JSONObject data){
    this.data = new JSONObjectConverter().convertToDatabaseColumn(data);
  }


}