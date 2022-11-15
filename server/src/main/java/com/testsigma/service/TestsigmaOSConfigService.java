package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import com.testsigma.repository.TestsigmaOSConfigRepository;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.OnboardingRequest;
import com.testsigma.web.request.TestsigmaAccountRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestsigmaOSConfigService {
  private final TestsigmaOSConfigRepository repository;
  private final IntegrationsService integrationsService;
  private final HttpClient httpClient;
  private final ServerService serverService;
  private final ApplicationConfig applicationConfig;

  @Value("${testsigma.platform.url}")
  @Getter
  private String testsigmaOsProxyUrl;

  public String getUrl() {
    return testsigmaOsProxyUrl;
  }

  public TestsigmaOSConfig find() {
    return repository.findFirstByIdIsNotNull();
  }

  public TestsigmaOSConfig save(TestsigmaOSConfig testsigmaOSConfig) {
    testsigmaOSConfig = this.repository.save(testsigmaOSConfig);
    return this.populateTestLab(testsigmaOSConfig);
  }

  private TestsigmaOSConfig populateTestLab(TestsigmaOSConfig testsigmaOSConfig) {
    Integrations testsigmaLab = integrationsService.findOptionalByApplication(Integration.TestsigmaLab).orElse(new Integrations());
    new Integrations();
    testsigmaLab.setName("Testsigma OS Lab");
    testsigmaLab.setPassword(testsigmaOSConfig.getAccessKey());
    testsigmaLab.setUsername(testsigmaOSConfig.getUserName());
    testsigmaLab.setWorkspace(Integration.TestsigmaLab);
    testsigmaLab.setWorkspaceId(Long.parseLong(Integration.TestsigmaLab.getId().toString()));
    testsigmaLab.setAuthType(IntegrationAuthType.AccessKey);
    testsigmaLab.setUrl(getUrl() + "/wd/hub");
    integrationsService.save(testsigmaLab);
    return testsigmaOSConfig;
  }

  public void createAccount(OnboardingRequest request) throws TestsigmaException {
    TestsigmaAccountRequest testsigmaAccountRequest = new TestsigmaAccountRequest();
    Server server = serverService.findOne();
    testsigmaAccountRequest.setFirstName(request.getFirstName());
    testsigmaAccountRequest.setLastName(request.getLastName());
    testsigmaAccountRequest.setEmail(request.getEmail());
    testsigmaAccountRequest.setProductUpdates(request.getIsSendUpdates());
    testsigmaAccountRequest.setCommunityAccess(request.getIsCommunityAccess());
    testsigmaAccountRequest.setServerUuid(server.getServerUuid());
    testsigmaAccountRequest.setServerVersion(applicationConfig.getServerVersion());
    testsigmaAccountRequest.setServerOs(server.getServerOs());
    testsigmaAccountRequest.setRegistrationType(request.getRegistrationType());

    HttpResponse<String> response =
      httpClient.post(this.getUrl() + "/api_public/accounts", getHeaders(), testsigmaAccountRequest,
        new TypeReference<>() {
        });
    if (response.getStatusCode() != HttpStatus.CREATED.value()) {
      log.error("Problem while creating account : " + response.getResponseText());
    }
  }

  public void getOTP(OnboardingRequest request) throws TestsigmaException {
    TestsigmaAccountRequest testsigmaAccountRequest = new TestsigmaAccountRequest();
    Server server = serverService.findOne();
    testsigmaAccountRequest.setEmail(request.getEmail());
    testsigmaAccountRequest.setFirstName(request.getFirstName());
    testsigmaAccountRequest.setLastName(request.getLastName());
    testsigmaAccountRequest.setServerUuid(server.getServerUuid());
    testsigmaAccountRequest.setProductUpdates(request.getIsSendUpdates());
    testsigmaAccountRequest.setCommunityAccess(request.getIsCommunityAccess());
    testsigmaAccountRequest.setRegistrationType(request.getRegistrationType());
    testsigmaAccountRequest.setRegistrationMedium(request.getRegistrationMedium());

    if (server.getConsent()) {
      testsigmaAccountRequest.setServerVersion(applicationConfig.getServerVersion());
      testsigmaAccountRequest.setServerOs(server.getServerOs());
    }
    HttpResponse<String> response =
      httpClient.post(this.getUrl() + "/api_public/accounts", getHeaders(), testsigmaAccountRequest,
        new TypeReference<>() {
        });
    if (response.getStatusCode() != HttpStatus.CREATED.value()) {
      log.error("Problem while creating account : " + response.getResponseText());
      throw new TestsigmaException("Problem while sending OTP");
    }
  }

  public void activate(String otp) throws TestsigmaException {
    HttpResponse<String> response =
      httpClient.get(this.getUrl() + "/api_public/accounts/activate/" + otp, getHeaders(),
        new TypeReference<>() {
        });
    int status = response.getStatusCode();
    if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      log.error("Wrong OTP  : " + response.getResponseText());
      throw new TestsigmaException("Wrong OTP");
    } else if (status != HttpStatus.ACCEPTED.value() && status != HttpStatus.OK.value()) {
      log.error("Problem while activating account : " + response.getResponseText());
      throw new TestsigmaException("Problem while activating account");
    }
    String token = response.getResponseText();
    TestsigmaOSConfig testsigmaOSConfig = this.find();
    testsigmaOSConfig.setAccessKey(token);
    save(testsigmaOSConfig);
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    return Lists.newArrayList(contentType);
  }

}
