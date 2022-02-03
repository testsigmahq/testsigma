package com.testsigma.service;

import com.testsigma.model.StorageConfig;
import com.testsigma.repository.StorageConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StorageConfigService {
  private final StorageConfigRepository storageConfigRepository;

  public StorageConfig getStorageConfig() {
    return storageConfigRepository.findAll(PageRequest.of(0, 1)).stream().findFirst().orElse(null);
  }

  public StorageConfig update(StorageConfig storageConfig) {
    return storageConfigRepository.save(storageConfig);
  }

}
