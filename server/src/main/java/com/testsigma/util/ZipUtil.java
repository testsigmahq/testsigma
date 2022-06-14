package com.testsigma.util;

import com.testsigma.exception.ZipFailedException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Log4j2
public class ZipUtil {
  public static File zipFolder(File sourceFolder, String fileName, File destFolder) throws IOException, ZipFailedException {
    String destFilePath = destFolder.getAbsolutePath()+ File.separator + fileName;
    try {
      System.out.println(sourceFolder.getAbsolutePath());
      FileOutputStream fos = new FileOutputStream(destFilePath);
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      zipFile(sourceFolder, sourceFolder.getName(), zipOut);
      zipOut.close();
      fos.close();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ZipFailedException(e.getMessage());
    }
    return new File(destFilePath);
  }

  public static File zipFile(File sourceFolder, String fileName, File destFolder) throws IOException, ZipFailedException {
    String destFilePath = destFolder.getAbsolutePath()+ File.separator + fileName;
    try {
      System.out.println(sourceFolder.getAbsolutePath());
      FileOutputStream fos = new FileOutputStream(destFilePath);
      ZipOutputStream zipOut = new ZipOutputStream(fos);
      for (File fileToZip : Objects.requireNonNull(sourceFolder.listFiles())) {
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
          zipOut.write(bytes, 0, length);
        }
        fis.close();
      }
      zipOut.close();
      fos.close();
    }
    catch (Exception e) {
    log.error(e.getMessage(), e);
    throw new ZipFailedException(e.getMessage());
  }
    return new File(destFilePath);
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

  public static File unZipFile(String path, File targetFolder) throws IOException {
    File zipFile = File.createTempFile(System.currentTimeMillis() + "", ".zip");
    FileUtils.copyURLToFile(new URL(path), zipFile);
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      File newFile = newFile(targetFolder, zipEntry);
      if (zipEntry.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
          throw new IOException("Failed to create directory " + newFile);
        }
      } else {
        // fix for Windows-created archives
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
          throw new IOException("Failed to create parent directory " + parent);
        }
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
      }
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
    zipFile.delete();
    if (targetFolder!=null && Objects.requireNonNull(targetFolder.listFiles()).length ==1 && Objects.requireNonNull(targetFolder.listFiles())[0].isDirectory())
      return targetFolder.listFiles()[0];
    return targetFolder;
  }

  public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());
    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();
    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }
}
