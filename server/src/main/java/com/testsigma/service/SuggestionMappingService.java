package com.testsigma.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.testsigma.dto.SuggestionDTO;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SuggestionMappingService {
  private static final String SUGGESTIONS_URI = "/api/suggestions";
  private final HttpClient httpClient;
  private final TestsigmaOSConfigService testsigmaOSConfigService;

  public List<SuggestionDTO> findAllByNaturalTextActionId(Integer naturalTextActionId) {
    List<SuggestionDTO> dtos = new ArrayList<>();
    try {
      HttpResponse<List<SuggestionDTO>> response = httpClient.get(getSuggestionsURL(naturalTextActionId), getHeaderList(), new TypeReference<>() {
      });
      dtos = response.getResponseEntity();
    } catch (Exception e) {
      log.error(e, e);
    }
    return dtos;
  }

  private List<Header> getHeaderList() {
    Header authorization = new BasicHeader(org.apache.http.HttpHeaders.AUTHORIZATION, "Bearer " + testsigmaOSConfigService.find().getAccessKey());
    Header contentType = new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, "application/json; " + StandardCharsets.UTF_8);
    return Lists.newArrayList(contentType, authorization);
  }

  private String getSuggestionsURL(Integer naturalTextActionId) {
    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(SUGGESTIONS_URI + "/" + naturalTextActionId).build().encode();
    return this.testsigmaOSConfigService.getUrl() + uriComponents.toUriString();
  }
}
