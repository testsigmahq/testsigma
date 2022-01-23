package com.testsigma.config;

import com.testsigma.service.*;
import com.testsigma.util.HttpClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Data
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class StorageServiceFactory {
  private static AwsS3StorageService awsS3StorageService;
  private static AzureBlobStorageService azureBlobStorageService;
  private static OnPremiseStorageService onPremiseStorageService;
  private static TestsigmaStorageService testsigmaStorageService;
  private final ApplicationConfig applicationConfig;
  private final HttpClient httpClient;
  private final JWTTokenService jwtTokenService;
  private final StorageConfigService storageConfigService;
  private final TestsigmaOSConfigService osConfigService;

  public StorageService getStorageService() {
    com.testsigma.model.StorageConfig storageConfig = storageConfigService.getStorageConfig();
    switch (storageConfig.getStorageType()) {
      case AWS_S3:
        if (awsS3StorageService != null && !isStorageConfigChanged(awsS3StorageService))
          return awsS3StorageService;
        awsS3StorageService = new AwsS3StorageService(storageConfigService.getStorageConfig(), applicationConfig, httpClient);
        return awsS3StorageService;

      case AZURE_BLOB:
        if (azureBlobStorageService != null && !isStorageConfigChanged(azureBlobStorageService))
          return azureBlobStorageService;
        azureBlobStorageService = new AzureBlobStorageService(storageConfigService.getStorageConfig(), applicationConfig, httpClient);
        return azureBlobStorageService;

      case ON_PREMISE:
        if (onPremiseStorageService != null && !isStorageConfigChanged(onPremiseStorageService))
          return onPremiseStorageService;
        onPremiseStorageService = new OnPremiseStorageService(storageConfigService.getStorageConfig(), applicationConfig,
          httpClient, jwtTokenService);
        return onPremiseStorageService;

      case TESTSIGMA:
        if (testsigmaStorageService != null)
          return testsigmaStorageService;
        testsigmaStorageService = new TestsigmaStorageService(storageConfigService.getStorageConfig(), applicationConfig,
          osConfigService, httpClient);
        return testsigmaStorageService;
    }
    return null;
  }

  public boolean isStorageConfigChanged(StorageService storageService) {
    com.testsigma.model.StorageConfig storageConfig = storageConfigService.getStorageConfig();
    com.testsigma.model.StorageConfig serviceStorageConfig = storageService.getStorageConfig();
    if ((storageConfig.getAwsBucketName() != null) && (!storageConfig.getAwsBucketName().equals(serviceStorageConfig.getAwsBucketName())))
      return true;
    if ((storageConfig.getAwsAccessKey() != null) && (!storageConfig.getAwsAccessKey().equals(serviceStorageConfig.getAwsAccessKey())))
      return true;
    if ((storageConfig.getAwsEndpoint() != null) && !storageConfig.getAwsEndpoint().equals(serviceStorageConfig.getAwsEndpoint()))
      return true;
    if ((storageConfig.getAwsRegion() != null) && (!storageConfig.getAwsRegion().equals(serviceStorageConfig.getAwsRegion())))
      return true;
    if ((storageConfig.getAwsSecretKey() != null) && (!storageConfig.getAwsSecretKey().equals(serviceStorageConfig.getAwsSecretKey())))
      return true;
    if ((storageConfig.getAzureContainerName() != null) && (!storageConfig.getAzureConnectionString().equals(serviceStorageConfig.getAzureConnectionString())))
      return true;
    if ((storageConfig.getAzureConnectionString() != null) && (!storageConfig.getAzureContainerName().equals(serviceStorageConfig.getAzureContainerName())))
      return true;
    if ((storageConfig.getOnPremiseRootDirectory() != null) && (!storageConfig.getOnPremiseRootDirectory().equals(serviceStorageConfig.getOnPremiseRootDirectory())))
      return true;
    return false;

  }

}
