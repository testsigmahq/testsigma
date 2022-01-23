/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

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

@Log4j2
@Entity
@Table(name = "suggestion_result_mapping")
@Data
public class SuggestionResultMapping implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "result")
  @Enumerated(EnumType.STRING)
  private SuggestionResultStatus result;

  @Column(name = "message")
  private String message;

  @Column(name = "meta_data")
  private String metaData;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;


  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "step_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestStepResult stepResult;

  @Column(name = "suggestion_id")
  private Integer suggestionId;

  @Column(name = "step_result_id")
  private Long stepResultId;

  public SuggestionResultMetaData getMetaData() {
    return new ObjectMapperService().parseJson(metaData, SuggestionResultMetaData.class);
  }

  public void setMetaData(SuggestionResultMetaData suggestionResultMetaData) {
    if (suggestionResultMetaData != null) {
      this.metaData = new ObjectMapperService().convertToJson(suggestionResultMetaData);
    }
  }

}
