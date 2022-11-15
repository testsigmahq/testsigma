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
public class FreshreleaseService {
  private final HttpClient httpClient;
  private final ApplicationConfig applicationConfig;

  @Getter
  @Setter
  private Integrations integrations;

  public EntityExternalMapping addIssue(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("title", mapping.getFields().get("title").toString());
    payload.put("description", mapping.getFields().get("description").toString());
    payload.put("issue_type_id", mapping.getFields().get("issue_type_id").toString());
    HttpResponse<JsonNode> response = httpClient.post(integrations.getUrl() + "/" + mapping.getFields().get("project") + "/issues", getHeaders(), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating freshrelease issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(response.getResponseEntity().get("issue").get("key").textValue());
    mapping.setMisc(response.getResponseText());
    return mapping;
  }

  public void unlink(EntityExternalMapping mapping) throws TestsigmaException {
    String project = mapping.getExternalId().split("-")[0];
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("content", "Unlinked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    Map<String, Object> issueDetails = fetchIssue(mapping);
    String link = ((Map<String, Object>) ((Map<String, Object>) issueDetails.get("issue")).get("links")).get("comments").toString();
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + link, getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while unlinking freshrelease issue with ::" + mapping.getFields());
    }
  }


  public Map<String, Object> fetchIssue(EntityExternalMapping mapping) throws TestsigmaException {
    String project = mapping.getExternalId().split("-")[0];
    HttpResponse<Map<String, Object>> response = httpClient.get(integrations.getUrl() + "/" + project + "/issues/" + mapping.getExternalId() + "?expand=names,renderedFields", getHeaders(), new TypeReference<Map<String, Object>>() {
    });
    return response.getResponseEntity();
  }

  public EntityExternalMapping link(EntityExternalMapping mapping) throws TestsigmaException {
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.putPOJO("content", "Linked to testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResult().getId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName());
    Map<String, Object> issueDetails = fetchIssue(mapping);
    String link = ((Map<String, Object>) ((Map<String, Object>) issueDetails.get("issue")).get("links")).get("comments").toString();
    HttpResponse<String> response = httpClient.post(integrations.getUrl() + link, getHeaders(), payload, new TypeReference<String>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while Linking freshrelease issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public JsonNode getIssuesList(String projectId, String summary) throws TestsigmaException {
    if (summary == null)
      summary = "";
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/" + projectId + "/issues?query_hash[0][condition]=title&query_hash[0][operator]=contains&query_hash[0][value]=" + summary, getHeaders(), new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode projects() throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/projects", getHeaders(), new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Token " + testAuth.getToken());
    List<Header> headers = Lists.newArrayList(contentType, authentication);
    HttpResponse<JsonNode> response = httpClient.get(testAuth.getUrl() + "/projects", headers, new TypeReference<JsonNode>() {
    });

    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getStatusMessage());
    return status;
  }

  public JsonNode issueTypes(String project) throws TestsigmaException {
    HttpResponse<JsonNode> response = httpClient.get(integrations.getUrl() + "/" + project + "/issue_types", getHeaders(), new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Token " + this.integrations.getToken());
    return Lists.newArrayList(contentType, authentication);
  }

}
