package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StorageType {
  ON_PREMISE("on-premise"),
  AWS_S3("aws-s3"),
  AZURE_BLOB("azure-blob"),
  TESTSIGMA("testsigma");

  private final String value;

  public static StorageType getByValue(String storageTypeInput) {
    if (ON_PREMISE.value.equals(storageTypeInput)) {
      return ON_PREMISE;
    } else if (AWS_S3.value.equals(storageTypeInput)) {
      return AWS_S3;
    } else if (AZURE_BLOB.value.equals(storageTypeInput)) {
      return AZURE_BLOB;
    } else if (TESTSIGMA.value.equals(storageTypeInput)) {
      return TESTSIGMA;
    }
    return ON_PREMISE;
  }
}
