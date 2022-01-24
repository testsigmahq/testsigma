package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.Integrations;
import com.testsigma.model.TestCaseResultExternalMapping;
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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class LinearService {
  private final HttpClient httpClient;
  private final ApplicationConfig applicationConfig;

  @Getter
  @Setter
  private Integrations integrations;


  public TestCaseResultExternalMapping addIssue(TestCaseResultExternalMapping mapping) throws TestsigmaException, URISyntaxException {
    String query = "mutation IssueCreate { issueCreate(input: {title: \"" + mapping.getFields().get("title").toString() + "\", teamId: \"" + mapping.getFields().get("teamId").toString() + "\", projectId: \"" + mapping.getFields().get("projectId").toString() + "\", description: \"" + mapping.getFields().get("description").toString() + "\", } )  {success issue {id title identifier}} }";
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("query", query);
    HttpResponse<JsonNode> response = httpClient.post("https://api.linear.app/graphql", getHeaders(integrations.getToken()), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Linear issue with ::" + mapping.getFields());
    }
    mapping.setExternalId(String.valueOf(response.getResponseEntity().get("data").get("issueCreate").get("issue").get("identifier").asText()));
    return mapping;
  }

  public TestCaseResultExternalMapping link(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    String comment = "Linked to testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName();
    String query = "mutation CommentCreate {commentCreate(input: {  body: \"" + comment + "\", issueId: \"" + mapping.getExternalId().replace("\"", "") + "\" } ) {lastSyncId}}";
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("query", query);
    HttpResponse<JsonNode> response = httpClient.post("https://api.linear.app/graphql", getHeaders(integrations.getToken()), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Linear issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public TestCaseResultExternalMapping unlink(TestCaseResultExternalMapping mapping) throws TestsigmaException {
    String comment = "UnLinked from testsigma results [" + applicationConfig.getServerUrl() + "/ui/td/test_case_results/" + mapping.getTestCaseResultId() + "]  :: " + mapping.getTestCaseResult().getTestCase().getName();
    String query = "mutation CommentCreate {commentCreate(input: {  body: \"" + comment + "\", issueId: \"" + mapping.getExternalId().replace("\"", "") + "\" } ) {lastSyncId}}";
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode payload = jnf.objectNode();
    payload.put("query", query);
    HttpResponse<JsonNode> response = httpClient.post("https://api.linear.app/graphql", getHeaders(integrations.getToken()), payload, new TypeReference<JsonNode>() {
    });
    if (response.getStatusCode() != HttpStatus.SC_OK) {
      log.error(response.getResponseText());
      throw new TestsigmaException("Problem while creating Linear issue with ::" + mapping.getFields());
    }
    return mapping;
  }

  public JsonNode teams() throws TestsigmaException, URISyntaxException {
    String query = "{teams{ nodes {id name }}}";
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", query);
    HttpResponse<JsonNode> response = httpClient.get(getHeaders(integrations.getToken()), builder, new TypeReference<JsonNode>() {
    });

    return response.getResponseEntity();
  }

  public JsonNode projects(String teamId) throws TestsigmaException, URISyntaxException {
    String query = "{team(id: \"" + teamId + "\"){ projects {nodes {id name} } }}";
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", query);
    HttpResponse<JsonNode> response = httpClient.get(getHeaders(integrations.getToken()), builder, new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssuesList(String projectId) throws TestsigmaException, URISyntaxException {
    String query = "{ project(id: \"" + projectId + "\") {issues { nodes { id title identifier description  priority team { id name}  project{id name} createdAt updatedAt } } }}";
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", query);
    HttpResponse<JsonNode> response = httpClient.get(getHeaders(integrations.getToken()), builder, new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode getIssue(String issueId) throws TestsigmaException, URISyntaxException {
    String query = "{ issue(id: \"" + issueId.replace("\"", "") + "\") {  id title identifier description priority team { id name}  project{id name} createdAt updatedAt} }";
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", query);
    HttpResponse<JsonNode> response = httpClient.get(getHeaders(integrations.getToken()), builder, new TypeReference<JsonNode>() {
    });
    return response.getResponseEntity();
  }

  public JsonNode testIntegration(IntegrationsRequest testAuth) throws TestsigmaException, URISyntaxException, IOException {
    URIBuilder builder = new URIBuilder("https://api.linear.app/graphql");
    builder.setParameter("query", "{teams{ nodes {id name }}}");
    HttpResponse<JsonNode> response = httpClient.get(getHeaders(testAuth.getToken()), builder, new TypeReference<JsonNode>() {
    });
    JsonNodeFactory jnf = JsonNodeFactory.instance;
    ObjectNode status = jnf.objectNode();
    status.put("status_code", response.getStatusCode());
    status.put("status_message", response.getResponseEntity());
    return status;
  }

  private List<Header> getHeaders(String token) {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, token);
    return Lists.newArrayList(contentType, authentication);
  }
}
