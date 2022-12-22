package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

@Data
@JsonListRootName(name = "suite_test_case_mappings")
@JsonRootName(value = "suite_test_case_mapping")
public class SuiteTestCaseMappingXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("SuiteId")
  private Long suiteId;
  @JsonProperty("TestCaseId")
  private Long testCaseId;
  @JsonProperty("Position")
  private Integer position;
}
