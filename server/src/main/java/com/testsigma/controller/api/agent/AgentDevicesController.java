/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller.api.agent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.config.URLConstants;
import com.testsigma.dto.IosXCTestResponseDTO;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.dto.AgentDeviceDTO;
import com.testsigma.dto.IosDeveloperImageDTO;
import com.testsigma.dto.IosWdaResponseDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.AgentDeviceMapper;
import com.testsigma.model.Agent;
import com.testsigma.model.AgentDevice;
import com.testsigma.model.ProvisioningProfileDevice;
import com.testsigma.service.AgentDeviceService;
import com.testsigma.service.AgentService;
import com.testsigma.service.ProvisioningProfileDeviceService;
import com.testsigma.service.TestsigmaOSConfigService;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import com.testsigma.util.ZipUtil;
import com.testsigma.web.request.AgentDeviceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

@Log4j2
@RestController(value = "agentAgentDevicesController")
@RequestMapping(value = {"/api/agents/{agentUuid}/devices"})
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AgentDevicesController {

  private final AgentDeviceService agentDeviceService;
  private final AgentDeviceMapper agentDeviceMapper;
  private final AgentService agentService;
  private final HttpClient httpClient;
  private final StorageServiceFactory storageServiceFactory;
  private final ProvisioningProfileDeviceService provisioningProfileDeviceService;
  private final TestsigmaOSConfigService testsigmaOSConfigService;
  private static final String XCTEST_RUNNER_FILEPATH = "https://s3.amazonaws.com/ios.testsigma.com/wda/WebDriverAgentRunner.xctest.zip";

  @RequestMapping(value = "/status", method = RequestMethod.PUT)
  public void syncInitialDeviceStatus(@PathVariable("agentUuid") String agentUuid) throws TestsigmaDatabaseException,
    ResourceNotFoundException {
    log.info(String.format("Received a PUT request api/agents/%s/devices/status ", agentUuid));
    Agent agent = agentService.findByUniqueId(agentUuid);
    agentDeviceService.updateDevicesStatus(agent.getId());
  }

  @RequestMapping(value = "/{uniqueId}", method = RequestMethod.GET)
  public AgentDeviceDTO show(@PathVariable("agentUuid") String agentUuid, @PathVariable("uniqueId") String uniqueId)
    throws ResourceNotFoundException {
    log.info(String.format("Received a GET request api/agents/%s/devices/%s ", agentUuid, uniqueId));
    Agent agent = agentService.findByUniqueId(agentUuid);
    AgentDevice agentDevice = agentDeviceService.findAgentDeviceByUniqueId(agent.getId(), uniqueId);
    return agentDeviceMapper.map(agentDevice);
  }

  @RequestMapping(method = RequestMethod.POST)
  public AgentDeviceDTO create(@PathVariable("agentUuid") String agentUuid,
                               @RequestBody AgentDeviceRequest agentDeviceRequest)
    throws TestsigmaDatabaseException, ResourceNotFoundException {
    log.info(String.format("Received a POST request api/agents/%s/devices . Request body is [%s]  ",
      agentUuid, agentDeviceRequest));
    Agent agent = agentService.findByUniqueId(agentUuid);
    AgentDevice agentDevice = agentDeviceMapper.map(agentDeviceRequest);
    agentDevice.setAgentId(agent.getId());
    agentDevice = agentDeviceService.create(agentDevice);
    return agentDeviceMapper.map(agentDevice);
  }

  @RequestMapping(value = "/{uniqueId}", method = RequestMethod.PUT)
  public AgentDeviceDTO update(@PathVariable("agentUuid") String agentUuid,
                               @PathVariable("uniqueId") String uniqueId,
                               @RequestBody AgentDeviceRequest agentDeviceRequest)
    throws TestsigmaDatabaseException, ResourceNotFoundException {
    log.info(String.format("Received a PUT request api/agents/%s/devices/%s . Request body is [%s]  ",
      agentUuid, uniqueId, agentDeviceRequest));
    Agent agent = agentService.findByUniqueId(agentUuid);
    AgentDevice agentDevice = agentDeviceService.findAgentDeviceByUniqueId(agent.getId(), uniqueId);
    agentDeviceMapper.map(agentDeviceRequest, agentDevice);
    agentDevice = agentDeviceService.update(agentDevice);
    return agentDeviceMapper.map(agentDevice);
  }

  @RequestMapping(value = "/{uniqueId}", method = RequestMethod.DELETE)
  public AgentDeviceDTO delete(@PathVariable("agentUuid") String agentUuid,
                               @PathVariable("uniqueId") String uniqueId)
    throws TestsigmaDatabaseException, ResourceNotFoundException {
    log.info(String.format("Received a DELETE request api/agents/%s/devices/%s", agentUuid, uniqueId));
    Agent agent = agentService.findByUniqueId(agentUuid);
    AgentDevice agentDevice = agentDeviceService.findAgentDeviceByUniqueId(agent.getId(), uniqueId);
    agentDeviceService.destroy(agentDevice);
    return agentDeviceMapper.map(agentDevice);
  }

  @RequestMapping(value = "/developer/{osVersion}/", method = RequestMethod.GET)
  public IosDeveloperImageDTO developer(@PathVariable("agentUuid") String agentUuid,
                                        @PathVariable("osVersion") String deviceOsVersion) throws TestsigmaException {
    log.info(String.format("Received a GET request api/agents/%s/devices/developer/%s", agentUuid, deviceOsVersion));
    HttpResponse<IosDeveloperImageDTO> response = httpClient.get(testsigmaOSConfigService.getUrl() +
      URLConstants.TESTSIGMA_OS_PUBLIC_IOS_IMAGE_FILES_URL + "/" + deviceOsVersion, getHeaders(), new TypeReference<>() {
    });

    IosDeveloperImageDTO iosDeveloperImageDTO = response.getResponseEntity();
    log.info("Ios developer image url DTO - " + iosDeveloperImageDTO);
    return iosDeveloperImageDTO;
  }

  @RequestMapping(value = "/{deviceUuid}/wda", method = RequestMethod.GET)
  public IosWdaResponseDTO deviceWdaUrl(@PathVariable String agentUuid, @PathVariable String deviceUuid)
    throws TestsigmaException {
    log.info(String.format("Received a GET request api/agents/%s/devices/%s/wda", agentUuid, deviceUuid));
    IosWdaResponseDTO iosWdaResponseDTO = new IosWdaResponseDTO();
    Agent agent = agentService.findByUniqueId(agentUuid);
    AgentDevice agentDevice = agentDeviceService.findAgentDeviceByUniqueId(agent.getId(), deviceUuid);
    String presignedUrl;
    if (!agentDevice.getIsEmulator()) {
      ProvisioningProfileDevice profileDevice = provisioningProfileDeviceService.findByAgentDeviceId(agentDevice.getId());
      if(profileDevice == null) {
        throw new TestsigmaException("could not find a provisioning profile for this device. Unable to fetch WDA");
      }
      presignedUrl = storageServiceFactory.getStorageService().generatePreSignedURL("wda/"
              + profileDevice.getProvisioningProfileId() + "/wda.ipa", StorageAccessLevel.READ, 180).toString();
    } else {
      String filePath = storageServiceFactory.getStorageService().downloadFromRemoteUrl("https://s3.amazonaws.com/ios.testsigma.com/wda/wda_simulator.ipa");
      storageServiceFactory.getStorageService().addFile("wda/wda_simulator.ipa", new File(filePath));
      presignedUrl = storageServiceFactory.getStorageService().generatePreSignedURLIfExists("wda/wda_simulator.ipa", StorageAccessLevel.READ, 180).get().toString();
    }
    iosWdaResponseDTO.setWdaPresignedUrl(presignedUrl);
    log.info("Ios Wda Response DTO - " + iosWdaResponseDTO);
    return iosWdaResponseDTO;
  }

  @RequestMapping(value = "/xctest", method = RequestMethod.GET)
  public IosXCTestResponseDTO deviceXCTestLocalPath(@PathVariable String agentUuid)
          throws TestsigmaException, IOException {
    log.info(String.format("Received a GET request api/agents/%s/devices/xctest", agentUuid));
    IosXCTestResponseDTO iosXCTestResponseDTO = new IosXCTestResponseDTO();
    //String filePath = storageServiceFactory.getStorageService().downloadFromRemoteUrl("");
    File destFolder = Files.createTempDirectory("wda_xctest").toFile();
    File unZippedFolder = new ZipUtil().unZipFile(XCTEST_RUNNER_FILEPATH, destFolder);
    iosXCTestResponseDTO.setXcTestLocalPath(unZippedFolder.getAbsolutePath() + "/WebDriverAgentRunner.xctest");
    log.info("Ios XCTest Response DTO - " + iosXCTestResponseDTO);
    return iosXCTestResponseDTO;
  }

  private ArrayList<Header> getHeaders() {
    ArrayList<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
    return headers;
  }
}
