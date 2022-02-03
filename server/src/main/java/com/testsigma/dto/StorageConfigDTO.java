package com.testsigma.dto;

import com.testsigma.model.StorageType;
import lombok.Data;

@Data
public class StorageConfigDTO {

  private Integer id;
  private StorageType storageType;
  private String awsBucketName;
  private String awsRegion;
  private String awsEndpoint;

  private String azureContainerName;
  private String azureConnectionString;

  private String onPremiseRootDirectory;
}
