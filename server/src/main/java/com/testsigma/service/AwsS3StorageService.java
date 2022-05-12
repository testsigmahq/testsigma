package com.testsigma.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.model.StorageConfig;
import com.testsigma.util.HttpClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Optional;

@Log4j2
public class AwsS3StorageService extends StorageService {

  private static AwsS3StorageService awsS3StorageService;
  private final AmazonS3 amazonS3;

  @Autowired
  public AwsS3StorageService(StorageConfig storageConfig, ApplicationConfig applicationConfig,
                             HttpClient httpClient) {
    log.info("Initializing AwsS3StorageService");
    this.storageConfig = storageConfig;
    this.applicationConfig = applicationConfig;
    this.httpClient = httpClient;

    this.amazonS3 = AmazonS3ClientBuilder.standard()
      .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(storageConfig.getAwsEndpoint(),
        storageConfig.getAwsRegion()))
      .withPathStyleAccessEnabled(true)
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(storageConfig.getAwsAccessKey(),
        storageConfig.getAwsSecretKey())))
      .build();
    log.info("Created AwsS3StorageService");
  }

  @Override
  public void addFile(String filePathFromRoot, File fileToAdd) {
    this.amazonS3.putObject(this.storageConfig.getAwsBucketName(), filePathFromRoot, fileToAdd);
  }

  @Override
  public void addFile(String filePathFromRoot, InputStream inputStream) {
    this.amazonS3.putObject(
      this.storageConfig.getAwsBucketName(),
      filePathFromRoot, inputStream, new ObjectMetadata()
    );
  }

  @Override
  public URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel,
                                  Integer expiryTimeInMinutes) {
    log.debug("Generating pre-signed URL for: " + relativeFilePathFromBase);
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, expiryTimeInMinutes);
    HttpMethod accessMethod = getHttpMethod(storageAccessLevel);
    URL presignedURL = this.amazonS3.generatePresignedUrl(storageConfig.getAwsBucketName(),
      relativeFilePathFromBase, cal.getTime(), accessMethod);
    log.debug("Aws S3 Pre-signed URL: " + presignedURL);
    return presignedURL;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel,
                                                    Integer expiryTimeInMinutes) {
    Optional<URL> returnURL = Optional.empty();
    ListObjectsV2Result result = this.amazonS3.listObjectsV2(storageConfig.getAwsBucketName(),
      relativeFilePathFromBase);
    if (!result.getObjectSummaries().isEmpty()) {
      log.debug("File exists, generating presigned URL for: " + relativeFilePathFromBase);
      returnURL = Optional.ofNullable(generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel, expiryTimeInMinutes));
    }
    return returnURL;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) {
    return generatePreSignedURLIfExists(relativeFilePathFromBase, storageAccessLevel,
      storageConfig.getAwsS3PreSignedURLTimeout());
  }

  @Override
  public URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) {
    return generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel, storageConfig.getAwsS3PreSignedURLTimeout());
  }

  @Override
  public void deleteFile(String filePath) {
    this.amazonS3.deleteObject(storageConfig.getAwsBucketName(), filePath);
    log.info("Deleted file from Aws S3: " + filePath);
  }

  private HttpMethod getHttpMethod(StorageAccessLevel storageAccessLevel) {
    if (storageAccessLevel == StorageAccessLevel.READ) {
      return HttpMethod.GET;
    } else if (storageAccessLevel == StorageAccessLevel.WRITE) {
      return HttpMethod.PUT;
    } else if (storageAccessLevel == StorageAccessLevel.DELETE) {
      return HttpMethod.DELETE;
    } else if (storageAccessLevel == StorageAccessLevel.FULL_ACCESS) {
      return HttpMethod.PUT;
    }
    return HttpMethod.GET;
  }

  @Override
  protected String getRootDirectory() {
    return storageConfig.getAwsBucketName();
  }

  public boolean validateCredentials() {
    return this.amazonS3.doesBucketExistV2(storageConfig.getAwsBucketName());
  }
}
