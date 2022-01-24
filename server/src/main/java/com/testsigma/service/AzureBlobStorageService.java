package com.testsigma.service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.model.StorageConfig;
import com.testsigma.util.HttpClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Optional;

@Log4j2
public class AzureBlobStorageService extends StorageService {

  private CloudBlobContainer container;

  @Autowired
  public AzureBlobStorageService(StorageConfig storageConfig, ApplicationConfig applicationConfig,
                                 HttpClient httpClient) {
    log.info("Initializing  AzureBlobStorageService");
    this.storageConfig = storageConfig;
    this.applicationConfig = applicationConfig;
    this.httpClient = httpClient;

    CloudBlobClient blobClient;
    try {
      blobClient = CloudStorageAccount.parse(storageConfig.getAzureConnectionString()).createCloudBlobClient();
      this.container = blobClient.getContainerReference(storageConfig.getAzureContainerName());
      container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(),
        new OperationContext());
    } catch (URISyntaxException e) {
      log.error(e.getMessage(), e);
    } catch (InvalidKeyException keyException) {
      log.error("Error while connecting to Azure storage service, Please verify given account details in " +
        "azure-blob.properties. " + keyException.getMessage(), keyException);
    } catch (StorageException storageException) {
      log.error("Error in getting container reference, Please verify given account details in " +
        "azure-blob.properties. " + storageException.getMessage(), storageException);
    }
  }

  @Override
  public void addFile(String filePathToAdd, File fileToAdd) {
    log.info("Adding file to Azure blob: " + filePathToAdd);
    filePathToAdd = removeLeadingSeparatorCharacterIfPresent(filePathToAdd);
    try {
      CloudBlockBlob fileBlob = container.getBlockBlobReference(filePathToAdd);
      fileBlob.uploadFromFile(fileToAdd.getAbsolutePath());
      log.info("Successfully added file to Azure blob: " + filePathToAdd);
    } catch (Exception e) {
      log.error("Error in creating file, Please verify given account details in azure-blob.properties. "
        + e.getMessage(), e);
    }
  }

  @Override
  public void addFile(String filePathToAdd, InputStream inputStream) {
    filePathToAdd = removeLeadingSeparatorCharacterIfPresent(filePathToAdd);
    log.info("Adding file to Azure blob: " + filePathToAdd);
    try {
      CloudBlockBlob fileBlob = container.getBlockBlobReference(filePathToAdd);
      fileBlob.upload(inputStream, -1);
      log.info("Successfully added file to Azure blob: " + filePathToAdd);
    } catch (Exception e) {
      log.error("Error in creating file using InputStream, Please verify given account details in " +
        "azure-blob.properties. " + e.getMessage(), e);
    }
  }

  public void addDirectory(String directoryPath) {
    directoryPath = removeLeadingSeparatorCharacterIfPresent(directoryPath);
    log.info("Adding directory to Azure blob: " + directoryPath);
    try {
      CloudBlobDirectory dir = container.getDirectoryReference(directoryPath);
      CloudBlockBlob dummyBlob = dir.getBlockBlobReference("createdir.txt");
      dummyBlob.uploadFromByteArray(new byte[0], 0, 0);
      log.info("Successfully created directory:" + directoryPath);
    } catch (Exception e) {
      log.error("Error in creating directory,Please verify given account details in azure-blob.properties. "
        + e.getMessage(), e);
    }
  }

  @Override
  public URL generatePreSignedURL(String relativeFilePathFromRoot, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes) {
    relativeFilePathFromRoot = removeLeadingSeparatorCharacterIfPresent(relativeFilePathFromRoot);
    log.debug("Generating Shared service access URL for: " + relativeFilePathFromRoot);
    URL url = null;
    try {
      CloudBlockBlob blob = container.getBlockBlobReference(relativeFilePathFromRoot);
      String sign = blob.generateSharedAccessSignature(getSharedAccessPolicy(storageAccessLevel, expiryTimeInMinutes),
        null);
      url = new URL(String.format("%s?%s", blob.getUri(), sign));
      log.debug("Azure blob Pre-signed URL :" + url);
    } catch (Exception e) {
      log.error("Unable to generate Shared Access URL,Please verify given account details in " +
        "azure-blob.properties. " + e.getMessage(), e);
    }
    return url;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes) {
    relativeFilePathFromBase = removeLeadingSeparatorCharacterIfPresent(relativeFilePathFromBase);
    log.debug("Generating Shared service access URL if exists: " + relativeFilePathFromBase);
    Optional<URL> returnURL = Optional.empty();
    try {
      CloudBlockBlob blob = container.getBlockBlobReference(relativeFilePathFromBase);
      if (blob.exists()) {
        log.debug("File exists, generating presigned URL for: " + relativeFilePathFromBase);
        returnURL = Optional.ofNullable(generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel, expiryTimeInMinutes));
      }
    } catch (Exception e) {
      log.error("Unable to generate Shared Access URL,Please verify given account details in " +
        "azure-blob.properties. " + e.getMessage(), e);
    }
    return returnURL;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) {
    relativeFilePathFromBase = removeLeadingSeparatorCharacterIfPresent(relativeFilePathFromBase);
    return generatePreSignedURLIfExists(relativeFilePathFromBase, storageAccessLevel,
      storageConfig.getAzureBlobPreSignedURLTimeout());
  }

  @Override
  public URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) {
    relativeFilePathFromBase = removeLeadingSeparatorCharacterIfPresent(relativeFilePathFromBase);
    return generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel,
      storageConfig.getAzureBlobPreSignedURLTimeout());
  }

  @Override
  public void deleteFile(String filePath) {
    filePath = removeLeadingSeparatorCharacterIfPresent(filePath);
    log.debug("Deleting file: " + filePath);
    try {
      CloudBlockBlob blob = container.getBlockBlobReference(filePath);
      blob.deleteIfExists();
      log.info("Successfully deleted file: " + filePath);
    } catch (Exception e) {
      log.error("Unable to delete file from Azure Blob,Please verify given account details in " +
        "azure-blob.properties. " + e.getMessage(), e);
    }
  }

  @Override
  protected String getRootDirectory() {
    return storageConfig.getAzureContainerName();
  }

  private SharedAccessBlobPolicy getSharedAccessPolicy(StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes) {
    SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
    policy.setPermissions(getBlobPermissions(storageAccessLevel));
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, expiryTimeInMinutes);
    policy.setSharedAccessExpiryTime(cal.getTime());
    return policy;
  }

  private EnumSet<SharedAccessBlobPermissions> getBlobPermissions(StorageAccessLevel storageAccessLevel) {
    if (storageAccessLevel == StorageAccessLevel.READ) {
      return EnumSet.of(SharedAccessBlobPermissions.READ);
    } else if (storageAccessLevel == StorageAccessLevel.WRITE) {
      return EnumSet.of(SharedAccessBlobPermissions.WRITE, SharedAccessBlobPermissions.READ,
        SharedAccessBlobPermissions.CREATE, SharedAccessBlobPermissions.ADD);
    } else if (storageAccessLevel == StorageAccessLevel.FULL_ACCESS) {
      return EnumSet.allOf(SharedAccessBlobPermissions.class);
    }
    return EnumSet.of(SharedAccessBlobPermissions.READ);
  }

  private String removeLeadingSeparatorCharacterIfPresent(String filePathToAdd) {
    if (filePathToAdd != null && filePathToAdd.startsWith("/")) {
      return filePathToAdd.substring(1);
    }
    return filePathToAdd;
  }
}
