package com.testsigma.util;

import com.testsigma.exception.ZipFailedException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;

@Log4j2
public class ZipUtil {
  public File zipFile(File sourceFolder, String name, File destFolder) throws IOException, ZipFailedException {
    System.out.println(sourceFolder.getAbsolutePath());
    ProcessBuilder processBuilder1 = new ProcessBuilder("/usr/bin/zip", "-r",
      destFolder.getAbsolutePath() + File.separator + name, ".");
    processBuilder1.directory(sourceFolder);
    processBuilder1.redirectInput(ProcessBuilder.Redirect.INHERIT);
    processBuilder1.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    Process process1;

    try {
      process1 = processBuilder1.start();
      process1.waitFor();
      if (process1.exitValue() != 0)
        throw new ZipFailedException(process1.getErrorStream().toString());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ZipFailedException(e.getMessage());
    }
    return new File(destFolder.getAbsolutePath() + File.separator + name);
  }
}
