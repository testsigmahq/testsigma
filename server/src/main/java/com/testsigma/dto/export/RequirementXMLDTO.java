package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
@JsonListRootName(name = "requirements")
@JsonRootName(value = "requirement")
public class RequirementXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("requirement-name")
  private String requirementName;
  @JsonProperty("requirement-description")
  private String requirementDescription;
  @JsonIgnore
  private Map<String, String> files;
  @JsonProperty("version-id")
  private Long workspaceVersionId;
  @JsonProperty("requirement-id")
  private Long requirementId;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("updated-by-id")
  private Long updatedById;
  @JsonProperty("custom-fields")
  private String customFields;
  @JsonProperty("start-time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp startTime;
  @JsonProperty("end-time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp endTime;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("requirement-key")
  private String requirementKey;
  @JsonProperty("requirement-priority-name")
  private String requirementPriorityName;
}
