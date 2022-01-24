package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Map;

@Data
@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefaultDataGeneratorsDetails {
  @JsonProperty("id")
  @Column(name = "default_data_generators_id")
  private Long id;


  @Type(type = "json")
  @JsonProperty("args")
  @Column(name = "default_data_generators_args", columnDefinition = "json")
  private Map<String, String> arguments;


}
