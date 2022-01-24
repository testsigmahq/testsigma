package com.testsigma.automator.utilities;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.testsigma.automator.constants.StorageConstants;
import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.automator.storage.StorageUploader;
import com.testsigma.automator.storage.StorageUploaderFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j2
public class ScreenshotUploadTask implements Runnable {
  private final TestDeviceEntity testDeviceEntity;
  private final StorageUploader storageUploader;
  List<ObjectNode> screenshots;
  String requestId;

  public ScreenshotUploadTask(List<ObjectNode> screenshots, String requestId, TestDeviceEntity testDeviceEntity) {
    this.screenshots = screenshots;
    this.requestId = requestId;
    this.testDeviceEntity = testDeviceEntity;
    this.storageUploader = new StorageUploaderFactory().getInstance(testDeviceEntity.getStorageType());
  }

  @Override
  public void run() {
    ThreadContext.put("X-Request-Id", requestId);
    for (ObjectNode image : screenshots) {
      upload(image.get(StorageConstants.STORAGE_FILE_PATH).asText(), image.get(StorageConstants.LOCAL_FILE_PATH).asText());
    }
  }

  private void upload(String destinationPath, String path) {
    try {
      storageUploader.upload(path.replace("\"", ""), destinationPath.replace("\"", ""));
    } finally {
      try {
        FileUtils.forceDelete(new File(path.replace("\"", "")));
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

}
