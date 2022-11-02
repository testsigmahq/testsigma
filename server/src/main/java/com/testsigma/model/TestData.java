/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.testsigma.converter.StringSetConverter;
import com.testsigma.converter.TestDataSetConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Entity
@Table(name = "test_data")
@ToString
@EqualsAndHashCode
public class TestData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  @Setter
  private Long id;

  @Column(name = "version_id")
  @Getter
  @Setter
  private Long versionId;


  @Column(name = "test_data_name")
  @Getter
  @Setter
  private String testDataName;


  @Column(name = "test_data")
  private String data;

  @Column(name = "imported_id")
  @Getter
  @Setter
  private Long importedId;

  @Column(name = "copied_from")
  @Getter
  @Setter
  private Long copiedFrom;

  @Column(name = "passwords")
  @Getter
  @Setter
  private String passwords;

  @Column(name = "created_date")
  @CreationTimestamp
  @Getter
  @Setter
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  @Getter
  @Setter
  private Timestamp updatedDate;

  @OneToMany(mappedBy = "testData", fetch = FetchType.LAZY)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @Getter
  @Setter
  private Set<TestCase> testCases;

  @Transient
  @Getter
  @Setter
  private Map<String, String> renamedColumns;


  public List<TestDataSet> getData() {
    return new TestDataSetConverter().convertToEntityAttribute(this.data);
  }

  public void setData(List<TestDataSet> dataSets) {
    this.data = new TestDataSetConverter().convertToDatabaseColumn(dataSets);
  }

  public List<String> getPasswords() {
    return new StringSetConverter().convertToEntityAttribute(this.passwords);
  }

  public void setPasswords(List<String> passwordList) {
    if (passwordList != null) {
      this.passwords = new StringSetConverter().convertToDatabaseColumn(passwordList);
    }
  }
}
