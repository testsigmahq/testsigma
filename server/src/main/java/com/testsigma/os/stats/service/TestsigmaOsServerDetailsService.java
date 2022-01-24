package com.testsigma.os.stats.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.config.URLConstants;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.service.ServerService;
import com.testsigma.service.TestsigmaOSConfigService;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestsigmaOsServerDetailsService {

  private final HttpClient httpClient;
  private final TestsigmaOSConfigService osConfigService;
  private final ServerService serverService;

  public String[] getTestsigmaLabIPs() throws TestsigmaException {
    HttpResponse<String[]> response = httpClient.get(osConfigService.getUrl() + URLConstants.TESTSIGMA_OS_TESTSIGMA_LAB_IP_URL
      , getHeaders(), new TypeReference<>() {
      });
    return (response.getStatusCode() == HttpStatus.OK.value() ? response.getResponseEntity() : null);
  }

  private ArrayList<Header> getHeaders() {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    return headers;
  }
}
