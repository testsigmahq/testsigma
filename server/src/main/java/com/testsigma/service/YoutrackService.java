/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.EntityExternalMapping;
import com.testsigma.model.Integrations;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.IntegrationsRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class YoutrackService {
  private final HttpClient httpClient;
  private final ApplicationConfig applicationConfig;

  @Getter
  @Setter
  private Integrations integrations;

  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    ObjectNode project = jnf.objectNode();
    project.put("id", mapping.getFields().get("projectId").toString());
    payload.put("summary", mapping.getFields().get("title").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    payload.put("project", project);

    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/api/issues?fields=id,idReadable,summary,description,reporter(login,name),created,updated", getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Youtrack issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(response.getResponseEntity().get("id").asText());
    mapping.setMisc(response.getResponseText());
    return mapping;
  }

  public void unlink(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("text", "Unlinked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    String url = integrations.getUrl() + "/api/issues/" + mapping.getExternalId() + "/comments";

    HttpResponse<JsonNode> response = httpClient.post(url, getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while unlinking Youtrack issue with ::" + mapping.getFields());
    }
  }


  public Map<String, Object> fetchIssue(EntityExternalMapping mapping) throws TestsigmaException {

    HttpResponse<Map<String, Object>> response = httpClient.get(integrations.getUrl()
        + "/api/issues/" + mapping.getExternalId() + "?fields=id,idReadable,summary,description,reporter(login,name),created,updated",
      getHeaders(), new TypeReference<Map<String, Object>>() {
      });
    return response.getResponseEntity();
  }


  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("text", "Linked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    String url = integrations.getUrl() + "/api/issues/" + mapping.getExternalId() + "/comments";

    HttpResponse<JsonNode> response = httpClient.post(url, getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while linking Youtrack issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public JsonNode getIssuesList() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/api/issues?fields=id,idReadable,summary,description",
      getHeaders(), new TypeReference<JsonNode>() {
      });
    return response.getResponseEntity();
  }

  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() +
      "/api/admin/projects?fields=id,name,shortName", getHeaders(), new TypeReference<JsonNode>() {
    });

    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while fetching the projects ");
    }
    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + testAuth.getToken());
    List<Header> headers = Lists.newArrayList(contentType, authentication);
    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() +
      "/api/admin/projects?fields=id,name,shortName", headers, new TypeReference<JsonNode>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.integrations.getToken());
    return Lists.newArrayList(contentType, authentication);
  }
}
