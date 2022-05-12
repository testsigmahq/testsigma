package com.testsigma.controller;

import com.testsigma.dto.StorageConfigDTO;
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

  @RequestMapping(method = RequestMethod.GET)
  public StorageConfigDTO get() {
    return storageConfigMapper.map(storageConfigService.getStorageConfig());
  }

  @RequestMapping(method = RequestMethod.PUT)
  public ResponseEntity<StorageConfigDTO> update(@RequestBody StorageConfigRequest storageConfigRequest) throws TestsigmaException {
    StorageConfig storageConfig = storageConfigMapper.map(storageConfigRequest);
    try {
      storageConfigService.validateCredentials(storageConfig);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    return new ResponseEntity<>(storageConfigMapper.map(storageConfigService.update(storageConfig)), HttpStatus.OK);
  }

}
