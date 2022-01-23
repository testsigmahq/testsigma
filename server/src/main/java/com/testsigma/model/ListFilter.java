/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "list_filters")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Data
@Log4j2
public class ListFilter implements Serializable {

  private static final ObjectMapper om = new ObjectMapper();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column(name = "version_id")
  private Long versionId;

  @Column(name = "query_hash")
  private String queryHash;

  @Column(name = "is_public")
  private Boolean isPublic = false;

  @Column(name = "is_default")
  private Boolean isDefault = false;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  public List<SearchCriteria> getSearchCriteria() {
    if (this.queryHash == null) {
      return null;
    }
    List<SearchCriteria> searchCriteria = new ArrayList<>();
    try {
      for (JsonNode node : om.readTree(queryHash)) {
        SearchOperation operation = SearchOperation.valueOf(node.get("operation").asText());
        Object value = node.get("value");
        if (node.get("value").isBoolean())
          value = node.get("value").asBoolean();
        else if (node.get("value").getNodeType() == JsonNodeType.STRING)
          value = node.get("value").asText();

        if (operation.equals(SearchOperation.IN)) {
          value = new ObjectMapper().convertValue(node.get("value"), ArrayList.class);
        }
        SearchCriteria criteria = new SearchCriteria(
          node.get("key").asText(),
          operation,
          value);
        searchCriteria.add(criteria);
      }
    } catch (IOException e) {
      log.error("Problem while decoding query hash - " + e.getMessage(), e);
    }
    return searchCriteria;
  }

}
