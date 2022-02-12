package com.testsigma.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.testsigma.dto.AddonDTO;
import com.testsigma.dto.AddonNaturalTextActionEntityDTO;
import com.testsigma.dto.AddonPluginTestDataFunctionEntityDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.model.*;
import com.testsigma.repository.AddonPluginTestDataFunctionParameterService;
import com.testsigma.repository.AddonPluginTestDataFunctionParameterRepository;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.web.request.addon.ActionUsageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProxyAddonService {
  private final static String ACTION_USAGE_URI = "/api/kibbutz/action_usage_details";
  private final static String PLUGINS_URI = "/api/kibbutz/plugin_versions";
  private final static String KIBBUTZ_LOGIN = "/api/kibbutz/login";
  private final HttpClient httpClient;
  private final AddonService addonService;
  private final AddonNaturalTextActionService addonNaturalTextActionService;
  private final AddonPluginTestDataFunctionService pluginTDFService;
  private final AddonPluginTestDataFunctionParameterService addonPluginTDFParameterService;
  private final AddonNaturalTextActionParameterService addonNaturalTextActionParameterService;
  private final AddonMapper addonMapper;
  private final TestsigmaOSConfigService testsigmaOSConfigService;
  private AddonPluginTestDataFunctionParameterRepository KibbutzPluginTDFParameterService;
  private AddonPluginTestDataFunctionService kibbutzPluginService;

  public void notifyActionUsing(AddonNaturalTextAction action) {
    try {
      log.debug("Notifying Action Usage ::" + action);
      ActionUsageRequest request = new ActionUsageRequest();
      request.setExternalUniqueId(action.getPlugin().getExternalUniqueId());
      request.setFullyQualifiedName(action.getFullyQualifiedName());
      log.debug("Notifying Action Usage ::" + request);
      HttpResponse<String> response =
        httpClient.post(this.testsigmaOSConfigService.getUrl() + ACTION_USAGE_URI, getHeaders(), request,
          new TypeReference<>() {
          });
      if (response.getStatusCode() != HttpStatus.ACCEPTED.value()) {
        log.error("Problem while notifying Action Usage ::" + response.getResponseText());
      }
    } catch (Exception exception) {
      log.error("Problem while notifying Action Usage ::" + action);
      log.error(exception.getMessage(), exception);
    }
  }


  public void notifyActionNotUsing(AddonNaturalTextAction action) {
    try {
      log.debug("Notifying Action Not Using ::" + action);
      HttpResponse<String> response =
        httpClient.delete(this.testsigmaOSConfigService.getUrl() + ACTION_USAGE_URI + "/" + action.getPlugin().getExternalUniqueId() + "?fullyQualifiedName=" + action.getFullyQualifiedName(), getHeaders(),
          new TypeReference<>() {
          });
      if (response.getStatusCode() != HttpStatus.ACCEPTED.value()) {
        log.error("Problem while notifying Action Not Using ::" + response.getResponseText());
      }
    } catch (Exception exception) {
      log.error("Problem while notifying Action Not Using ::" + action);
      log.error(exception.getMessage(), exception);
    }
  }

  public AddonDTO fetchPluginFromService(String pluginVersionId) {
    AddonDTO addonDTO = null;
    try {
      log.debug("Fetching plugin info for id - " + pluginVersionId);
      HttpResponse<AddonDTO> response =
        httpClient.get(this.testsigmaOSConfigService.getUrl() + PLUGINS_URI + "/" + pluginVersionId, getHeaders(),
          new TypeReference<>() {
          });
      if (response.getStatusCode() != HttpStatus.OK.value()) {
        log.error("Problem while Fetching plugin info ::" + response.getResponseText());
      } else {
        addonDTO = response.getResponseEntity();
      }
    } catch (Exception exception) {
      log.error("Problem Fetching plugin info for id - " + pluginVersionId);
      log.error(exception.getMessage(), exception);
    }
    return addonDTO;
  }

  public AddonNaturalTextActionEntityDTO fetchPluginEntity(Long addonId) throws ResourceNotFoundException {
    AddonNaturalTextAction addonNaturalTextAction = addonNaturalTextActionService.findById(addonId);
    Addon addon = addonService.findById(addonNaturalTextAction.getAddonId());
    List<AddonNaturalTextActionParameter> pluginActionParameters = addonNaturalTextActionParameterService.findByAddonId(addonId);
    AddonDTO addonDTO = fetchPluginFromService(addon.getExternalInstalledVersionUniqueId());
    AddonNaturalTextActionEntityDTO addonNaturalTextActionEntityDTO = new AddonNaturalTextActionEntityDTO();
    addonNaturalTextActionEntityDTO.setId(addonId);
    addonNaturalTextActionEntityDTO.setNaturalText(addonNaturalTextAction.getNaturalText());
    addonNaturalTextActionEntityDTO.setClassPath(addonDTO.getClassesPath().toString());
    addonNaturalTextActionEntityDTO.setFullyQualifiedName(addonNaturalTextAction.getFullyQualifiedName());
    addonNaturalTextActionEntityDTO.setVersion(addon.getVersion());
    addonNaturalTextActionEntityDTO.setModifiedHash(addon.getModifiedHash());
    addonNaturalTextActionEntityDTO.setExternalInstalledVersionUniqueId(addon.getExternalInstalledVersionUniqueId());
    addonNaturalTextActionEntityDTO.setPluginParameters(addonMapper.mapParamsEntity(pluginActionParameters));
    return addonNaturalTextActionEntityDTO;
  }

  public AddonPluginTestDataFunctionEntityDTO fetchPluginTestDataFunctionEntities(Long testDataFunctionId) throws ResourceNotFoundException {
    AddonPluginTestDataFunction testDataFunction = pluginTDFService.findById(testDataFunctionId);
    Addon addonPlugin = addonService.findById(testDataFunction.getAddonId());
    List<AddonPluginTestDataFunctionParameter> pluginTDFParameters = addonPluginTDFParameterService.findByTestDataFunctionId(testDataFunction.getAddonId());
    AddonDTO kibbutzPluginDTO = fetchPluginFromService(addonPlugin.getExternalInstalledVersionUniqueId());
    AddonPluginTestDataFunctionEntityDTO addonPluginActionEntityDTO = new AddonPluginTestDataFunctionEntityDTO();
    addonPluginActionEntityDTO.setId(testDataFunctionId);
    addonPluginActionEntityDTO.setDisplayName(testDataFunction.getDisplayName());
    addonPluginActionEntityDTO.setClassPath(kibbutzPluginDTO.getClassesPath().toString());
    addonPluginActionEntityDTO.setFullyQualifiedName(testDataFunction.getFullyQualifiedName());
    addonPluginActionEntityDTO.setVersion(addonPlugin.getVersion());
    addonPluginActionEntityDTO.setModifiedHash(addonPlugin.getModifiedHash());
    addonPluginActionEntityDTO.setExternalInstalledVersionUniqueId(addonPlugin.getExternalInstalledVersionUniqueId());
    addonPluginActionEntityDTO.setPluginParameters(addonMapper.mapTDFParamsEntity(pluginTDFParameters));
    return addonPluginActionEntityDTO;
  }

  private List<Header> getHeaders() {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.testsigmaOSConfigService.find().getAccessKey());
    return Lists.newArrayList(contentType, authentication);
  }

  public URL ssoURL(String redirectURI) {
    URL loginToken = null;
    HttpResponse<String> response = null;
    try {
      response = httpClient.get(this.testsigmaOSConfigService.getUrl() + KIBBUTZ_LOGIN + "?redirectURI=" + redirectURI, getHeaders(),
        new TypeReference<>() {
        });
      if (response != null) {
        loginToken = new URL(response.getResponseEntity());
      }
    } catch (TestsigmaException | MalformedURLException e) {
      log.error("Unable to generate Pre-Signed URL - " + e.getMessage(), e);
    }
    return loginToken;
  }
}
