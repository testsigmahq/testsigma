package com.testsigma.service;

import com.amazonaws.HttpMethod;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.config.URLConstants;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.StorageConfig;
import com.testsigma.util.HttpClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Optional;


@Log4j2
public class OnPremiseStorageService extends StorageService {
  public static final String STORAGE_SIGNATURE = "X-ON-PREM-STORAGE-SIGNATURE";
  private static final String FILE_PROTOCOL = "file:";
  private final JWTTokenService jwtTokenService;

  public OnPremiseStorageService(StorageConfig storageConfig, ApplicationConfig applicationConfig,
                                 HttpClient httpClient, JWTTokenService jwtTokenService) {
    log.info("Initializing OnPremiseStorageService");
    this.storageConfig = storageConfig;
    this.applicationConfig = applicationConfig;
    this.httpClient = httpClient;
    this.jwtTokenService = jwtTokenService;
  }

  private String getFilePathRelativeToRoot(String filePath) {
    if (!filePath.trim().toLowerCase().startsWith(FILE_PROTOCOL)) {
      filePath = getRootDirectory() + File.separator + filePath;
    } else {
      filePath = filePath.substring(5);
    }
    return filePath;
  }

  @Override
  public void addFile(String filePathToAdd, File fileToAdd) {
    String filePathFromRoot = getFilePathRelativeToRoot(filePathToAdd);
    log.info(String.format("Copying file from %s to %s", fileToAdd, filePathFromRoot));
    File newFile = new File(filePathFromRoot);
    try {
      if (newFile.exists()) {
        FileUtils.forceDeleteOnExit(newFile);
      }
      FileUtils.copyFile(fileToAdd, newFile);
    } catch (IOException e) {
      log.error("Unable to create a new file", e);
    }

  }

  @Override
  public void addFile(String filePathToAdd, InputStream inputStream) {
    String filePathFromRoot = getFilePathRelativeToRoot(filePathToAdd);
    log.info(String.format("Copying data from input stream to %s", filePathFromRoot));
    File newFile = new File(filePathFromRoot);
    try {
      if (newFile.exists()) {
        FileUtils.forceDelete(newFile);
      }
      FileUtils.copyInputStreamToFile(inputStream, new File(filePathFromRoot));
    } catch (IOException e) {
      log.error("Unable to create a new file from input stream", e);
    }
  }

  public void addDirectory(String directoryPathFromRoot) {
    File newDir = new File(getFilePathRelativeToRoot(directoryPathFromRoot));
    try {
      FileUtils.forceMkdir(newDir);
    } catch (IOException e) {
      log.error("Unable to create a new directory", e);
    }
  }

  @Override
  public URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes) {
    log.info("Generating pre-signed URL for:" + relativeFilePathFromBase);
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, expiryTimeInMinutes);
    URL preSignedURL = null;
    try {
      String token = jwtTokenService.generateAttachmentToken(relativeFilePathFromBase, cal.getTime(), getHttpMethod(storageAccessLevel));
      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
      queryParams.add(STORAGE_SIGNATURE, token);
      UriComponents uriComponents =
        UriComponentsBuilder.fromUriString(URLConstants.PRESIGNED_BASE_URL + "/{key}").queryParams(queryParams)
          .build().expand(relativeFilePathFromBase).encode();
      preSignedURL = new URL(applicationConfig.getServerUrl() + uriComponents.toUriString());
      log.info("Before normalizing - " + preSignedURL.toString());
      preSignedURL = preSignedURL.toURI().normalize().toURL();
      log.info("After normalizing - " + preSignedURL.toString());
    } catch (Exception e) {
      log.error("Unable to construct pre-signed URL object from path," + relativeFilePathFromBase, e);
    }
    return preSignedURL;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes) {
    log.debug("Generating File URL if exists:" + relativeFilePathFromBase);
    Optional<URL> returnURL = Optional.empty();
    String rootDirectory = getRootDirectory();
    if (rootDirectory.contains("\\")){
     relativeFilePathFromBase = relativeFilePathFromBase.replaceAll("/","\\");
    }
    File file = Paths.get(rootDirectory, relativeFilePathFromBase).toFile();
    if (file.exists()) {
      log.debug("File exists, generating pre-signed URL for:" + relativeFilePathFromBase);
      returnURL = Optional.ofNullable(generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel, expiryTimeInMinutes));
    }
    return returnURL;
  }

  @Override
  public Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) {
    return generatePreSignedURLIfExists(relativeFilePathFromBase, storageAccessLevel, 600);
  }

  @Override
  public URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) {
    return generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel, 600);
  }

  @Override
  public String downloadToLocal(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) throws TestsigmaException {
    return getRootDirectory() + File.separator + relativeFilePathFromBase;
  }

  @Override
  public void deleteFile(String filePath) {
    FileUtils.deleteQuietly(new File(filePath));
  }

  @Override
  protected String getRootDirectory() {
    return Paths.get(applicationConfig.getDataDir(), storageConfig.getOnPremiseRootDirectory()).toString();
  }

  public String getAbsoluteFilePath(String relativePathFromRoot) {
    return getRootDirectory() + File.separator + relativePathFromRoot;
  }

  private HttpMethod getHttpMethod(StorageAccessLevel storageAccessLevel) {
    switch (storageAccessLevel) {
      case WRITE:
      case FULL_ACCESS:
        return HttpMethod.POST;
      case DELETE:
        return HttpMethod.DELETE;
      default:
        return HttpMethod.GET;
    }
  }
}
