package com.testsigma.automator.storage;

import com.testsigma.automator.constants.StorageType;

public class StorageUploaderFactory {
  public StorageUploader getInstance(StorageType storageType) {
    if (storageType == StorageType.AWS_S3) {
      return new AwsS3Uploader();
    } else if (storageType == StorageType.AZURE_BLOB) {
      return new AzureBlobUploader();
    } else if (storageType == StorageType.TESTSIGMA) {
      return new TestsigmaUploader();
    } else {
      return new OnPremiseUploader();
    }

  }
}
