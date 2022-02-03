package com.testsigma.util;

import com.testsigma.exception.ZipFailedException;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
public class ZipUtil {
  public static File zipFile(File sourceFolder, String name, File destFolder) throws IOException, ZipFailedException {
    try {
      System.out.println(sourceFolder.getAbsolutePath());
      FileOutputStream fos = new FileOutputStream(destFolder.getAbsolutePath() + File.separator + name);
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      zipFile(sourceFolder, sourceFolder.getName(), zipOut);
      zipOut.close();
      fos.close();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ZipFailedException(e.getMessage());
    }
    return new File(destFolder.getAbsolutePath() + File.separator + name);
  }


  private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
    if (fileToZip.isHidden()) {
      return;
    }
    if (fileToZip.isDirectory()) {
      if (fileName.endsWith("/")) {
        zipOut.putNextEntry(new ZipEntry(fileName));
        zipOut.closeEntry();
      } else {
        zipOut.putNextEntry(new ZipEntry(fileName + "/"));
        zipOut.closeEntry();
      }
      File[] children = fileToZip.listFiles();
      for (File childFile : children) {
        zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
      }
      return;
    }
    FileInputStream fis = new FileInputStream(fileToZip);
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipOut.write(bytes, 0, length);
    }
    fis.close();
  }
}
