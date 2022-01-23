/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.http;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
public abstract class HttpClient {
  public final static String BEARER = "Bearer";
  public final static String BASIC_AUTHORIZATION = "Basic";

  public static String encodedCredentials(String input) {
    String authHeader = "";
    try {
      byte[] encodedAuth = Base64.encodeBase64(input.getBytes(StandardCharsets.ISO_8859_1));
      if (encodedAuth != null) {
        authHeader = new String(encodedAuth);
      }
    } catch (Exception ignore) {
    }
    return authHeader;
  }

  public abstract <T> HttpResponse<T> get(String url, TypeReference<T> typeReference) throws IOException;

  public abstract <T> HttpResponse<T> put(String url, Object data, TypeReference<T> typeReference) throws IOException;

  public abstract <T> HttpResponse<T> post(String url, Object data, TypeReference<T> typeReference) throws IOException;

  public abstract <T> HttpResponse<T> downloadFile(String url, String filePath) throws IOException;


  public abstract <T> HttpResponse<T> get(String url, TypeReference<T> typeReference, String authHeader) throws IOException;

  public abstract <T> HttpResponse<T> put(String url, Object data, TypeReference<T> typeReference, String authHeader) throws IOException;

  public abstract <T> HttpResponse<T> post(String url, Object data, TypeReference<T> typeReference, String authHeader) throws IOException;

  public abstract <T> HttpResponse<T> downloadFile(String url, String filePath, String authHeader) throws IOException;

  public abstract void closeHttpClient();
}
