package com.testsigma.web.request;

import com.testsigma.model.StorageType;
import lombok.Data;

@Data
public class StorageConfigRequest {
  private Integer id;
  private StorageType storageType;
  private String awsBucketName;
  private String awsRegion;
  private String awsEndpoint;
  private String awsAccessKey;
  private String awsSecretKey;

  private String azureContainerName;
  private String azureConnectionString;

  private String onPremiseRootDirectory;
}
