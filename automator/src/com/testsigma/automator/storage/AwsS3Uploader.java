package com.testsigma.automator.storage;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@Log4j2
public class AwsS3Uploader extends StorageUploader {


  @Override
  public void upload(String srcFilePath, String destinationURL) {
    if (!isFileExists(srcFilePath)) {
      log.info("File not found. Unable to upload - " + srcFilePath);
      return;
    }
    OutputStream outputStream = null;
    InputStream inputStream = null;
    log.info(String.format("Uploading test asset to Aws S3, presigned-URL:%s, localFilePath:%s", destinationURL, srcFilePath));

    try {
      inputStream = new FileInputStream(srcFilePath.replace("\"", ""));
      URL url = new URL(destinationURL.replace("\"", ""));
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("PUT");
      outputStream = connection.getOutputStream();
      IOUtils.copy(inputStream, outputStream);
      outputStream.flush();
      log.debug("S3 response on screenshot upload ::" + connection.getResponseCode() + " - " + connection.getResponseMessage());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    } finally {
      try {
        inputStream.close();
        if (outputStream != null) {
          outputStream.close();
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
