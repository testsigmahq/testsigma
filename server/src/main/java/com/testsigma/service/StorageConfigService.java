package com.testsigma.service;

import com.testsigma.config.ApplicationConfig;
import com.testsigma.model.StorageConfig;
import com.testsigma.repository.StorageConfigRepository;
import com.testsigma.util.HttpClient;
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
  private final ApplicationConfig applicationConfig;
  private final HttpClient httpClient;

  public StorageConfig getStorageConfig() {
    return storageConfigRepository.findAll(PageRequest.of(0, 1)).stream().findFirst().orElse(null);
  }

  public StorageConfig update(StorageConfig storageConfig) {
    return storageConfigRepository.save(storageConfig);
  }

    public boolean validateCredentials(StorageConfig storageConfigReq) {
      switch (storageConfigReq.getStorageType()) {
        case AWS_S3:
          if (storageConfigReq.getAwsAccessKey()==null && storageConfigReq.getAwsSecretKey()==null) return true;
          AwsS3StorageService awsS3StorageService = new AwsS3StorageService(storageConfigReq, applicationConfig, httpClient);
          return awsS3StorageService.validateCredentials();
        case AZURE_BLOB:
          if(storageConfigReq.getAzureConnectionString() == null) return true;
          AzureBlobStorageService azureBlobStorageService = new AzureBlobStorageService(storageConfigReq, applicationConfig, httpClient);
          return azureBlobStorageService.validateCredentials();
        case ON_PREMISE:
        case TESTSIGMA:
          return true;
      }
      return false;
    }
}
