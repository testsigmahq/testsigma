package com.testsigma.automator.entity;

import com.testsigma.automator.constants.AuthorizationTypes;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class RestfulStepEntity implements Serializable, Cloneable {

  /**
   *
   */
  private Long id;
  private String url;
  private String method;
  private String requestHeaders;
  private String payload;
  private String status;
  private String headerCompareType;
  private String responseHeaders;
  private String responseCompareType;
  private String response;
  private Long stepId;
  private String expectedResultType;
  private Boolean storeMetadata;
  private String headerRuntimeData;
  private String bodyRuntimeData;
  private Boolean followRedirects;
  private AuthorizationTypes authorizationType;
  private String authorizationValue;
  private Boolean isMultipart;

  public List<Integer> getResultCompareTypes() {

    List<Integer> compareTypes = new ArrayList<Integer>();
    if (StringUtils.isNotBlank(expectedResultType)) {
      String[] types = expectedResultType.split(",");
      for (int i = 0; i < types.length; i++) {
        compareTypes.add(Integer.parseInt(types[i]));
      }

    }

    Collections.sort(compareTypes);
    return compareTypes;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {

    return super.clone();
  }
}
