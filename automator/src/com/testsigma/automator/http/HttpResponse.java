/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.automator.exceptions.AgentDeletedException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
@Log4j2
@JsonIgnoreProperties
public class HttpResponse<T> {
  private static final ObjectMapper om =
    new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private int statusCode;
  private String statusMessage;
  private String responseText;
  private T responseEntity;
  private Header[] responseHeaders;

  public HttpResponse(org.apache.http.HttpResponse response, TypeReference<T> typeReference) throws IOException {
    this.statusCode = response.getStatusLine().getStatusCode();
    this.statusMessage = response.getStatusLine().getReasonPhrase();
    this.responseHeaders = response.getAllHeaders();
    HttpEntity entity = response.getEntity();
    this.responseText = entity == null ? "" : EntityUtils.toString(entity);
    log.debug("Http Response details: Code - " + statusCode + ", Message - " + statusMessage);
    log.info("Response entityString: " + responseText);
    setParsedResponse(typeReference);
  }

  public HttpResponse(org.apache.http.HttpResponse response) throws IOException {
    this.statusCode = response.getStatusLine().getStatusCode();
    this.statusMessage = response.getStatusLine().getReasonPhrase();
    this.responseHeaders = response.getAllHeaders();
  }

  public void setParsedResponse(TypeReference<T> typeReference) throws AgentDeletedException {
    if (this.statusCode == HttpStatus.PRECONDITION_FAILED.value()
      && !this.responseText.contains("Not a local agent registration")) {
      throw new AgentDeletedException();
    } else if ((this.statusCode < 300) && (typeReference != null)) {
      try {
        if (typeReference.getType().equals(String.class)) {
          this.responseEntity = (T) this.responseText;
        } else {
          this.responseEntity = om.readValue(this.responseText, typeReference);
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public Map<String, String> getHeadersMap() {
    Map<String, String> responseHeaders = new HashMap<>();
    for (Header header : this.responseHeaders) {
      responseHeaders.put(header.getName(), header.getValue());
    }

    return responseHeaders;

  }

}
