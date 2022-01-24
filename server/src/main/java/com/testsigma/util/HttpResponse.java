/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

@Data
@Log4j2
@JsonIgnoreProperties
public class HttpResponse<T> {
  private static final ObjectMapperService objectMapperService = new ObjectMapperService();
  private int statusCode;
  private String statusMessage;
  private String responseText;
  private T responseEntity;
  @Getter
  private List<Cookie> cookies;

  public HttpResponse(org.apache.http.HttpResponse response, TypeReference<T> typeReference)
    throws IOException {
    this.statusCode = response.getStatusLine().getStatusCode();
    this.statusMessage = response.getStatusLine().getReasonPhrase();
    if (response.getEntity() != null)
      this.responseText = EntityUtils.toString(response.getEntity());
    log.info("Http Response details: Code - " + statusCode + ", Message - " + statusMessage);
    log.info("Response entityString: " + StringUtils.abbreviate(responseText, 500));
    setParsedResponse(typeReference);
  }

  public HttpResponse(org.apache.http.HttpResponse response, TypeReference<T> typeReference, List<Cookie> cookies)
    throws IOException {
    this.statusCode = response.getStatusLine().getStatusCode();
    this.statusMessage = response.getStatusLine().getReasonPhrase();
    if (response.getEntity() != null)
      this.responseText = EntityUtils.toString(response.getEntity());
    this.cookies = cookies;
    log.info("Http Response details: Code - " + statusCode + ", Message - " + statusMessage);
    log.info("Response entityString: " + StringUtils.abbreviate(responseText, 500));
    setParsedResponse(typeReference);
  }

  public void setParsedResponse(TypeReference<T> typeReference) throws IOException {
    if ((this.statusCode < 300) && (typeReference != null)) {
      if (typeReference.getType().equals(String.class)) {
        this.responseEntity = (T) this.responseText;
      } else {
        this.responseEntity = objectMapperService.parseJson(this.responseText, typeReference);
      }
    }
  }

}
