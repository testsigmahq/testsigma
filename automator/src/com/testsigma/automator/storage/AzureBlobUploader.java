package com.testsigma.automator.storage;

import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;


@Log4j2
public class AzureBlobUploader extends StorageUploader {

  @Override
  public void upload(String srcFilePath, String destinationURL) {
    if (!isFileExists(srcFilePath)) {
      log.info("File not found. Unable to upload - " + srcFilePath);
      return;
    }
    log.info(String.format("Uploading test asset to Azure Blob, presigned-URL:%s, localFilePath:%s", destinationURL, srcFilePath));
    try {
      CloudBlockBlob blob = new CloudBlockBlob(URI.create(destinationURL));
      File source = new File(srcFilePath);
      blob.upload(new FileInputStream(source), source.length());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
