package com.testsigma.automator.storage;

import java.io.File;

public abstract class StorageUploader {
  public abstract void upload(String srcFilePath, String destinationURL);

  public boolean isFileExists(String filePath) {
    return new File(filePath).exists();
  }
}
