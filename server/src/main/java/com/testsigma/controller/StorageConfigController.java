package com.testsigma.controller;

import com.testsigma.config.StorageServiceFactory;
import com.testsigma.dto.StorageConfigDTO;
import com.testsigma.exception.InvalidStorageCredentialsException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.StorageConfigMapper;
import com.testsigma.model.StorageConfig;
import com.testsigma.service.StorageConfigService;
import com.testsigma.web.request.StorageConfigRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage_config")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageConfigController {
  private final StorageConfigService storageConfigService;
  private final StorageConfigMapper storageConfigMapper;
  private final StorageServiceFactory storageServiceFactory;

  @RequestMapping(method = RequestMethod.GET)
  public StorageConfigDTO get() {
    return storageConfigMapper.map(storageConfigService.getStorageConfig());
  }

  @RequestMapping(method = RequestMethod.PUT)
  public StorageConfigDTO update(@RequestBody StorageConfigRequest storageConfigRequest) throws TestsigmaException {
    StorageConfig storageConfig = storageConfigMapper.map(storageConfigRequest);
    try {
      storageServiceFactory.validateCredentials(storageConfig);
    } catch (Exception e) {
      throw new InvalidStorageCredentialsException(e.getMessage());
    }
    StorageConfig storageConfigDto = storageConfigService.update(storageConfig);
    return storageConfigMapper.map(storageConfigDto);
  }

}
