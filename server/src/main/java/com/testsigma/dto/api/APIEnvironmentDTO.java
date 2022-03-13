package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

@Data
public class APIEnvironmentDTO {
  private Long id;
  private String name;
  private String description;
  @JsonProperty("updated_date")
  private Timestamp updatedDate;
  @JsonProperty("created_date")
  private Timestamp createdDate;
  @JsonSerialize(using = JSONObjectSerializer.class)
  private JSONObject parameters;
}
