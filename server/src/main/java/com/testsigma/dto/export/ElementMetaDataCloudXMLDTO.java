package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;


@Data
@Log4j2
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonRootName(value = "UiIdentifierMetadata")
public class ElementMetaDataCloudXMLDTO {
  @JsonProperty("XPath")
  private String xPath;
  @JsonProperty("CurrentElement")
  private String currentElement;
  @JsonProperty("testdata")
  @JsonSerialize(using = JSONArrayXMLSerializer.class)
  @JsonDeserialize(using = JSONArrayXMLDeserializer.class)
  private JSONArray testData = new JSONArray();
}
