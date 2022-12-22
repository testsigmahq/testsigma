package com.testsigma.service;


import com.testsigma.config.ApplicationConfig;
import com.testsigma.model.StorageAccessLevel;
import com.testsigma.model.StorageType;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.StorageConfig;
import com.testsigma.util.HttpClient;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

@Log4j2
@Data
public abstract class StorageService {

  protected StorageConfig storageConfig;
  protected ApplicationConfig applicationConfig;
  protected HttpClient httpClient;

  public abstract void addFile(String filePathFromRoot, File fileToAdd);

  public abstract void addFile(String filePathFromRoot, InputStream inputStream);

  public abstract URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes);

  public abstract Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel, Integer expiryTimeInMinutes);

  public abstract Optional<URL> generatePreSignedURLIfExists(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel);

  public abstract URL generatePreSignedURL(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel);

  public abstract void deleteFile(String filePath);

  protected abstract String getRootDirectory();

  public StorageType getStorageType() {
    return storageConfig.getStorageType();
  }

  public String downloadToLocal(String relativeFilePathFromBase, StorageAccessLevel storageAccessLevel) throws TestsigmaException {
    return downloadFromRemoteUrl(generatePreSignedURL(relativeFilePathFromBase, storageAccessLevel).toString());
  }

  public byte[] getFileByteArray(String preSignedURL) throws IOException {
    if (getStorageType() == StorageType.ON_PREMISE && preSignedURL.toLowerCase().startsWith("file:")) {
      preSignedURL = preSignedURL.trim().substring(5);
    }
    return FileUtils.readFileToByteArray(new File(preSignedURL));
  }

  public String downloadFromRemoteUrl(String appRemoteUrl) throws TestsigmaException {
    InputStream appInputStream = null;
    FileOutputStream appOutputStream = null;
    try {
      String appLocalPath = Paths.get(System.getProperty("java.io.tmpdir"), getFileName(appRemoteUrl)).toString();
      CloseableHttpClient client = createHttpClient();
      HttpGet request = new HttpGet(appRemoteUrl);

      HttpResponse response = client.execute(request);
      HttpEntity entity = response.getEntity();
      appInputStream = entity.getContent();
      File appLocalFile = new File(appLocalPath);
      appOutputStream = new FileOutputStream(appLocalFile);
      IOUtils.copy(appInputStream, appOutputStream);
      appInputStream.close();
      appOutputStream.close();
      return appLocalPath;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    } finally {
      try {
        if (appInputStream != null)
          appInputStream.close();
        if (appOutputStream != null)
          appOutputStream.close();
      } catch (IOException ignore) {
      }
    }
  }

  public String getFileName(String appRemoteUrl) throws TestsigmaException {
    try {
      URL url = new URL(appRemoteUrl);
      return FilenameUtils.getName(url.getPath());
    } catch (MalformedURLException ex) {
      throw new TestsigmaException(ex);
    }
  }

  private CloseableHttpClient createHttpClient() {
    RequestConfig config = RequestConfig.custom()
      .setSocketTimeout(10 * 60 * 1000)
      .setConnectionRequestTimeout(60 * 1000)
      .setConnectTimeout(60 * 1000)
      .build();
    return HttpClients.custom().setDefaultRequestConfig(config)
      .build();
  }
}
