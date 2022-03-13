package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.model.RestRequestMetadata;
import com.testsigma.model.RestResponseMetadata;
import com.testsigma.model.StepResultForLoopMetadata;
import com.testsigma.model.TestDataType;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StepResultMetadataDTO {

  private Long id;
  private String action;
  private TestDataType testDataType;
  private String testDataValue;
  private String attribute;
  @JsonSerialize(using = JSONObjectSerializer.class)
  private JSONObject additionalData;
  private RestResponseMetadata restResult;
  private RestRequestMetadata reqEntity;
  @JsonProperty("for_loop")
  private StepResultForLoopMetadata forLoop;
  private StepDetailsMetadataDTO stepDetails;

}
