/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import com.testsigma.util.HttpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PlatformsService {

  private static final String PLATFORMS_BASE_URL = "/api/platforms";
  private static final String PLATFORMS_BASE_PUBLIC_URL = "/api_public/platforms";
  private static final String PLATFORM_OS_VERSION_URL = "/{platform}/{osVersion}";
  private static final String PLATFORM_OS_VERSION_BY_ID_URL = "/{platformOsVersionId}/os_version";
  private static final String PLATFORM_OS_VERSIONS_URL = "/{platform}/os_versions";
  private static final String PLATFORM_BROWSERS_URL = "/{platform}/{osVersion}/browsers";
  private static final String PLATFORM_BROWSER_VERSION_URL = "/{platform}/{osVersion}/browser/{browserName}/{browserVersion}";
  private static final String PLATFORM_BROWSER_VERSION_BY_ID_URL = "/{platformBrowserVersionId}/browser_version";
  private static final String PLATFORM_BROWSER_VERSIONS_URL = "/{platform}/{osVersion}/browser/{browserName}/versions";
  private static final String PLATFORM_SCREEN_RESOLUTION_URL = "/{platform}/{osVersion}/resolution/{resolution}";
  private static final String PLATFORM_SCREEN_RESOLUTION_BY_ID_URL = "/{platformScreenResolutionId}/screen_resolution";
  private static final String PLATFORM_SCREEN_RESOLUTIONS_URL = "/{platform}/{osVersion}/screen_resolutions";
  private static final String PLATFORM_DEVICES_URL = "/{platform}/devices";
  private static final String PLATFORM_DEVICE_URL = "/{platform}/{osVersion}/device/{deviceName}";
  private static final String PLATFORM_DEVICE_BY_ID_URL = "/{platformDeviceId}/device";

  private final HybridPlatformService hybridPlatformService;
  private final PrivateGridService privateGridService;
  private final IntegrationsService integrationsService;
  private final HttpClient httpClient;
  private final TestsigmaOSConfigService testsigmaOSConfigService;

  public List<Platform> getSupportedPlatforms(WorkspaceType workspaceType,
                                              TestPlanLabType testPlanLabType) throws TestsigmaException {

    if (testPlanLabType != TestPlanLabType.PrivateGrid) {
      com.testsigma.util.HttpResponse<List<Platform>> response = httpClient.get(getSupportedPlatformsUrl(
                      workspaceType, testPlanLabType),
              getHeaders(testPlanLabType), new TypeReference<>() {
              });
      if (response.getStatusCode() < 300) {
        return response.getResponseEntity();
      } else {
        return new ArrayList<>();
      }
    }
    else {
     return this.privateGridService.getAllPlatforms();
    }
  }

  public List<PlatformOsVersion> getPlatformOsVersions(Platform platform,
                                                       WorkspaceType workspaceType,
                                                       TestPlanLabType testPlanLabType) throws TestsigmaException {

    com.testsigma.util.HttpResponse<List<PlatformOsVersion>> response = httpClient.get(getPlatformOsVersionsUrl(
      platform, workspaceType, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return new ArrayList<>();
    }
  }

  public PlatformOsVersion getPlatformOsVersion(Platform platform, String osVersion,
                                                WorkspaceType workspaceType,
                                                TestPlanLabType testPlanLabType) throws TestsigmaException {

    com.testsigma.util.HttpResponse<PlatformOsVersion> response = httpClient.get(getPlatformOsVersionUrl(platform,
      osVersion, workspaceType, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }

  public PlatformOsVersion getPlatformOsVersion(Long platformOsVersionId,
                                                TestPlanLabType testPlanLabType) throws TestsigmaException {

    com.testsigma.util.HttpResponse<PlatformOsVersion> response = httpClient.get(getPlatformOsVersionUrl(platformOsVersionId,
        testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }


  public List<Browsers> getPlatformSupportedBrowsers(Platform platform, String osVersion,
                                                     TestPlanLabType testPlanLabType)
    throws TestsigmaException {
    if (testPlanLabType!=TestPlanLabType.PrivateGrid) {
      com.testsigma.util.HttpResponse<List<Browsers>> response = httpClient.get(getBrowsersNamesUrl(platform,
              osVersion, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
      });
      if (response.getStatusCode() < 300) {
        return response.getResponseEntity();
      } else {
        return new ArrayList<>();
      }
    }
    else {
      return this.privateGridService.getPlatformSupportedBrowsers(platform);
    }
  }

  public List<PlatformBrowserVersion> getPlatformBrowsers(Platform platform, String osVersion,
                                                          Browsers browserName,
                                                          TestPlanLabType testPlanLabType) throws TestsigmaException {

    if (testPlanLabType != TestPlanLabType.PrivateGrid) {
      com.testsigma.util.HttpResponse<List<PlatformBrowserVersion>> response = httpClient.get(getBrowsersUrl(
              platform, osVersion, browserName.toString(), testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
      });
      if (response.getStatusCode() < 300) {
        return response.getResponseEntity();
      } else {
        return new ArrayList<>();
      }
    }
    else {
    return this.privateGridService.getPlatformBrowserVersions( platform, browserName);
    }
  }

  public PlatformBrowserVersion getPlatformBrowserVersion(Platform platform, String osVersion,
                                                          Browsers browserName, String browserVersion,
                                                          TestPlanLabType testPlanLabType) throws TestsigmaException {

    com.testsigma.util.HttpResponse<PlatformBrowserVersion> response = httpClient.get(getBrowserUrl(platform,
      osVersion, browserName.toString(), browserVersion, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }

  public PlatformBrowserVersion getPlatformBrowserVersion(Long platformBrowserVersionId,
                                                          TestPlanLabType testPlanLabType) throws TestsigmaException {

    com.testsigma.util.HttpResponse<PlatformBrowserVersion> response = httpClient.get(getBrowserUrl(platformBrowserVersionId,
        testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }

  public List<PlatformScreenResolution> getPlatformScreenResolutions(Platform platform, String osVersion,
                                                                     TestPlanLabType testPlanLabType)
    throws TestsigmaException {

    com.testsigma.util.HttpResponse<List<PlatformScreenResolution>> response = httpClient.get(getScreenResolutionsUrl(
      platform, osVersion, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return new ArrayList<>();
    }
  }

  public PlatformScreenResolution getPlatformScreenResolution(Long platformScreenResolutionId,
                                                              TestPlanLabType testPlanLabType)
    throws TestsigmaException {

    com.testsigma.util.HttpResponse<PlatformScreenResolution> response = httpClient.get(getScreenResolutionUrl(
      platformScreenResolutionId, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }

  public List<PlatformDevice> getPlatformDevices(Platform platform, String osVersion,
                                                 TestPlanLabType testPlanLabType) throws TestsigmaException {
    List<String> osVersions = new ArrayList<>();
    osVersions.add(osVersion);
    return getPlatformDevices(platform, osVersions, testPlanLabType);
  }

  public List<PlatformDevice> getPlatformDevices(Platform platform, List<String> osVersions,
                                                 TestPlanLabType testPlanLabType) throws TestsigmaException {
    com.testsigma.util.HttpResponse<List<PlatformDevice>> response = httpClient.get(getDevicesUrl(platform,
      osVersions, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return new ArrayList<>();
    }
  }

  public PlatformDevice getPlatformDevice(Platform platform, String osVersion, String deviceName,
                                          TestPlanLabType testPlanLabType) throws TestsigmaException {
    com.testsigma.util.HttpResponse<PlatformDevice> response = httpClient.get(getDeviceUrl(platform,
      osVersion, deviceName, testPlanLabType), getHeaders(testPlanLabType), new TypeReference<>() {
    });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }

  public PlatformDevice getPlatformDevice(Long platformDeviceId,
                                          TestPlanLabType testPlanLabType) throws TestsigmaException {
    com.testsigma.util.HttpResponse<PlatformDevice> response = httpClient.get(getDeviceUrl(platformDeviceId, testPlanLabType),
      getHeaders(testPlanLabType), new TypeReference<>() {
      });
    if (response.getStatusCode() < 300) {
      return response.getResponseEntity();
    } else {
      return null;
    }
  }

  public String getDriverPath(Platform platform, String browserVersion, Browsers browsers, String versionFolder) {

    return hybridPlatformService.getDriverPath(platform.name(), browserVersion, browsers, versionFolder);
  }

  public void closePlatformSession(TestPlanLabType testPlanLabType) throws TestsigmaException {
    if (testPlanLabType == TestPlanLabType.Hybrid) {
      hybridPlatformService.closePlatformSession();
    } else {
      throw new TestsigmaException("Execution Lab Type " + testPlanLabType + " Not Supported");
    }
  }

  private String getPlatformsBaseUrl(TestPlanLabType testPlanLabType) {
    Integrations integrations = getExternalApplicationConfig();
    if((integrations != null) && testPlanLabType == TestPlanLabType.TestsigmaLab) {
      return testsigmaOSConfigService.getUrl() + PLATFORMS_BASE_URL;
    } else {
      return testsigmaOSConfigService.getUrl() + PLATFORMS_BASE_PUBLIC_URL;
    }
  }

  private Integrations getExternalApplicationConfig() {
    try {
      Integrations integrations = integrationsService.findByApplication(
        Integration.TestsigmaLab);
      return integrations;
    } catch (IntegrationNotFoundException e) {
      log.error(e.getMessage());
    }
    return null;
  }

  private String getSupportedPlatformsUrl(WorkspaceType workspaceType, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("applicationType", workspaceType.toString());
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString("").queryParams(queryParams).build().encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getPlatformOsVersionUrl(Platform platform, String osVersion, WorkspaceType workspaceType,
                                         TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("applicationType", workspaceType.toString());
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_OS_VERSION_URL).queryParams(queryParams).build()
        .expand(platform.toString(), osVersion).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getPlatformOsVersionUrl(Long platformOsVersionId,
                                         TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_OS_VERSION_BY_ID_URL).queryParams(queryParams).build()
        .expand(platformOsVersionId).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getPlatformOsVersionsUrl(Platform platform, WorkspaceType workspaceType,
                                          TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("applicationType", workspaceType.toString());
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_OS_VERSIONS_URL).queryParams(queryParams).build()
        .expand(platform.toString()).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getBrowsersNamesUrl(Platform platform, String osVersion, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_BROWSERS_URL).queryParams(queryParams).build()
        .expand(platform.toString(), osVersion).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getBrowserUrl(Platform platform, String osVersion, String browserName,
                               String browserVersion, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_BROWSER_VERSION_URL).queryParams(queryParams).build()
        .expand(platform.toString(), osVersion, browserName, browserVersion).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getBrowserUrl(Long platformBrowserVersionId, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_BROWSER_VERSION_BY_ID_URL).queryParams(queryParams).build()
        .expand(platformBrowserVersionId).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getBrowsersUrl(Platform platform, String osVersion, String browserName,
                                TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_BROWSER_VERSIONS_URL).queryParams(queryParams).build()
        .expand(platform.toString(), osVersion, browserName).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getScreenResolutionUrl(Long platformScreenResolutionId,
                                        TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_SCREEN_RESOLUTION_BY_ID_URL).queryParams(queryParams).build()
        .expand(platformScreenResolutionId).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getScreenResolutionsUrl(Platform platform, String osVersion, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_SCREEN_RESOLUTIONS_URL).queryParams(queryParams).build()
        .expand(platform.toString(), osVersion).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getDevicesUrl(Platform platform, List<String> osVersions, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());
    queryParams.add("osVersions", String.join(",", osVersions));

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_DEVICES_URL).queryParams(queryParams).build()
        .expand(platform.toString()).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getDeviceUrl(Platform platform, String osVersion, String deviceName, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_DEVICE_URL).queryParams(queryParams).build()
        .expand(platform.toString(), osVersion, deviceName).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private String getDeviceUrl(Long platformDeviceId, TestPlanLabType testPlanLabType) {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("executionLabType", testPlanLabType.toString());

    UriComponents uriComponents =
      UriComponentsBuilder.fromUriString(PLATFORM_DEVICE_BY_ID_URL).queryParams(queryParams).build()
        .expand(platformDeviceId).encode();
    return getPlatformsBaseUrl(testPlanLabType) + uriComponents.toUriString();
  }

  private ArrayList<Header> getHeaders(TestPlanLabType testPlanLabType) {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

    if (testPlanLabType == TestPlanLabType.TestsigmaLab) {
      try {
        Integrations integrations = integrationsService.findByApplication(
          Integration.TestsigmaLab);
        if(integrations != null) {
          headers.add(new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer "
            + integrations.getPassword()));
        }
      } catch (IntegrationNotFoundException e) {
        log.error(e.getMessage());
      }
    }
    return headers;
  }
}
