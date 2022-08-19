package com.testsigma.service;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.dto.PreSignedRequestDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.StorageConfig;
import com.testsigma.util.HttpClient;
import com.testsigma.util.HttpResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Log4j2
public class TestsigmaStorageService extends StorageService {

  private final String TS_REQUEST_PATH = "/api/attachments";
  private final String TS_PRESIGNED_URL_PATH = "/generate_pre_signed_url";
  private final String TS_KEY_EXISTS = "/is_pre_signed_url_exists";

  private final TestsigmaOSConfigService openSourceConfigService;

  public TestsigmaStorageService(StorageConfig storageConfig, ApplicationConfig applicationConfig,
                                 TestsigmaOSConfigService openSourceConfigService,
                                 HttpClient httpClient) {
    log.info("Initializing TestsigmaStorageService");
    this.storageConfig = storageConfig;
    this.applicationConfig = applicationConfig;
    this.httpClient = httpClient;
    this.openSourceConfigService = openSourceConfigService;
    log.info("Created TestsigmaStorageService");
  }

  @Override
  public void addFile(String filePathFromRoot, File fileToAdd) {
    try {
      log.info("Adding file to storage:" + filePathFromRoot);
      addFile(filePathFromRoot, new FileInputStream(fileToAdd));
    } catch (FileNotFoundException e) {
      log.error("Unable to add File " + e.getMessage(), e);
    }
  }


  @Override
  public void addFile(String filePathFromRoot, InputStream inputStream) {
    log.info("Adding file to storage: " + filePathFromRoot);
    try {
      httpClient.post(String.format("%s?key=%s", getRequestURI(), filePathFromRoot),
        getHeaders(false), new File(filePathFromRoot).getName(), inputStream,
        new TypeReference<>() {
        }, null);
    } catch (TestsigmaException e) {
      log.error("Unable to add file to Testsigma Storage, filePath:" + filePathFromRoot + " - " + e.getMessage(),
        e);
    }
  }


  @Override
  public URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes) {
    log.info("Generating pre-signed URL for:" + relativeFilePathFromBase);
    URL presignedURL = null;
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, expiryTimeInMinutes);
    PreSignedRequestDTO preSignedRequestDTO = new PreSignedRequestDTO();
    preSignedRequestDTO.setKey(relativeFilePathFromBase.startsWith("/") ? relativeFilePathFromBase : "/" + relativeFilePathFromBase);
    preSignedRequestDTO.setExpiration(new Timestamp(cal.getTime().getTime()));
    preSignedRequestDTO.setMethod(getHttpMethod(storageAccessLevel));
    HttpResponse<String> response = null;
    try {
      response = httpClient.post(getRequestURI() + TS_PRESIGNED_URL_PATH, getHeaders(true), preSignedRequestDTO,
        new TypeReference<>() {
        });
    } catch (TestsigmaException e) {
      log.error("Unable to generate presigned URL - " + e.getMessage(), e);
    }
    if (response != null) {
      try {
        presignedURL = new URL(response.getResponseEntity());
      } catch(MalformedURLException e) {
        log.error(e.getMessage(), e);
      }
    }
    return presignedURL;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel,
                                                    Integer expiryTimeInMinutes) {
    log.info("Generate PresignedURL if exists");
    Optional<URL> returnURL = Optional.empty();
    HttpResponse<Boolean> response = null;
    try {
      response = httpClient.get(String.format("%s%s?key=%s", getRequestURI(), TS_KEY_EXISTS, relativeFilePathFromBase),
        getHeaders(true), new TypeReference<>() {
        });
      if (Boolean.parseBoolean(response.getResponseText()))
 {
        log.debug("File exists, generating presigned URL for: " + relativeFilePathFromBase);
        returnURL = Optional.ofNullable(generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel, expiryTimeInMinutes));
      }
    } catch (TestsigmaException e) {
      log.error("Unable to delete file from Storage, filePath: " + relativeFilePathFromBase + " - "
        + e.getMessage(), e);
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
    return generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel,
      storageConfig.getAwsS3PreSignedURLTimeout());
  }

  @Override
  public void deleteFile(String filePathFromRoot) {
    log.info("Deleting file from storage: " + filePathFromRoot);
    try {
      httpClient.delete(String.format("%s?key=%s", getRequestURI(), filePathFromRoot),
        getHeaders(true), new TypeReference<>() {
        });
    } catch (TestsigmaException e) {
      log.error("Unable to delete file from Storage, filePath: " + filePathFromRoot + " - "
        + e.getMessage(), e);
    }
  }

  private HttpMethod getHttpMethod(StorageAccessLevel storageAccessLevel) {
    if (storageAccessLevel == StorageAccessLevel.READ) {
      return HttpMethod.GET;
    } else if (storageAccessLevel == StorageAccessLevel.WRITE) {
      return HttpMethod.POST;
    } else if (storageAccessLevel == StorageAccessLevel.DELETE) {
      return HttpMethod.DELETE;
    } else if (storageAccessLevel == StorageAccessLevel.FULL_ACCESS) {
      return HttpMethod.POST;
    }
    return HttpMethod.GET;
  }

  @Override
  protected String getRootDirectory() {
    return storageConfig.getAwsBucketName();
  }

  private String getRequestURI() {
    return String.format("%s%s", openSourceConfigService.getUrl(), TS_REQUEST_PATH);
  }

  private List<Header> getHeaders(boolean addContentType) {
    Header contentType = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    Header authentication = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openSourceConfigService.find().getAccessKey());
    if (addContentType) {
      return Lists.newArrayList(contentType, authentication);
    }
    return Lists.newArrayList(authentication);
  }
}

