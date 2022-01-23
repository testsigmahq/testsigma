package com.testsigma.automator.drivers;

import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.utilities.OsUtil;
import com.testsigma.automator.utilities.PathUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j2
public class DriversUpdateService {
  public static final String BROWSER_STR = "browser";
  public static final String VERSION_STR = "version";
  private final String driversFolderPath;
  private final String osType;

  public DriversUpdateService() {
    driversFolderPath = PathUtil.getInstance().getDriversPath();
    osType = new OsUtil().getOsType();
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

  public void syncBrowserDriver(TestDeviceEntity testDeviceEntity) throws AutomatorException {
    if (testDeviceEntity.getExecutionLabType() == ExecutionLabType.Hybrid) {
      log.info("Trying to check and sync browser driver for environment - " + testDeviceEntity.getId());
      Map<String, String> browserDetailsMap = getBrowserDetailsFromEnvironment(testDeviceEntity.getEnvSettings());
      log.info(String.format("Retrieved Browser Details - Name: %s - Version: %s", browserDetailsMap.get(BROWSER_STR),
        browserDetailsMap.get(VERSION_STR)));
      if (StringUtils.isBlank(browserDetailsMap.get(BROWSER_STR))) {
        return;
      }
      OsBrowserType browserType = OsBrowserType.getOsBrowserType(browserDetailsMap.get(BROWSER_STR));
      String browserVersion = browserDetailsMap.get(VERSION_STR);
      String driverPath = testDeviceEntity.getEnvSettings().getHybridBrowserDriverPath();
      syncBrowserDriver(browserType, browserVersion, driverPath);
    } else {
      log.info(String.format("Execution Lab Type <%s> doesn't require driver sync. Skipping it",
        testDeviceEntity.getExecutionLabType()));
    }
  }

  public void syncBrowserDriver(OsBrowserType browserType, String browserVersion, String driverPath)
    throws AutomatorException {
    log.info(String.format("Trying to check and sync browser - %s - %s - %s", browserType, browserVersion,
      driverPath));
    try {
      if (!isDriverExecutableExists(driverPath)) {
        log.info(String.format("%s : %s - Browser driver does not exist. downloading it", browserType, browserVersion));
        updateDriver(browserType, browserVersion);
        log.info(String.format("%s : %s - Finished downloading the browser driver", browserType, browserVersion));
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  private Map<String, String> getBrowserDetailsFromEnvironment(TestDeviceSettings envSettings) {
    Map<String, String> browserDetails = new HashMap<>();
    String browser = envSettings.getBrowser();
    String browserVersion = envSettings.getBrowserVersion();
    browserDetails.put(BROWSER_STR, browser);
    browserDetails.put(VERSION_STR, browserVersion);
    return browserDetails;
  }

  private void updateDriver(OsBrowserType browserName, String versionStr)
    throws IOException {
    if (browserName == OsBrowserType.Chrome) {
      downloadAndCopyDriverFile(Browsers.GoogleChrome, versionStr);
    } else if (browserName == OsBrowserType.Firefox) {
      downloadAndCopyDriverFile(Browsers.MozillaFirefox, versionStr);
    } else if (browserName == OsBrowserType.Edge) {
      downloadAndCopyDriverFile(Browsers.MicrosoftEdge, versionStr);
    } else if (browserName == OsBrowserType.Safari) {
    }
  }

  private void downloadAndCopyDriverFile(Browsers browser, String majorVersion) throws IOException {
    String browserVersion = Float.parseFloat(majorVersion) + "";
    String zipFileName = browserVersion.replace(".", "_") + ".zip";
    String driverDownloadUrl = getDriverDownloadURL(osType, browser, zipFileName);
    File driverLocalPath = Paths.get(driversFolderPath, browser.getBrowserFolderName(), zipFileName).toFile();
    log.info(String.format("Copying Driver File From %s to %s", driverDownloadUrl, driverLocalPath));
    FileUtils.copyURLToFile(new URL(driverDownloadUrl), driverLocalPath);
    File driverVersionFolder = Paths.get(driversFolderPath, browser.getBrowserFolderName(), browserVersion).toFile();
    unzipDriver(driverLocalPath, driverVersionFolder);
  }

  private boolean isDriverExecutableExists(String path) {
    String dirPath = driversFolderPath + path;
    log.info("Verifying if driver version folder exists: " + dirPath);
    File browserVersionDirFile = new File(dirPath);
    if (browserVersionDirFile.exists()) {
      File driverFile = new File(browserVersionDirFile.getAbsolutePath());
      return driverFile.exists() && driverFile.isFile();
    }
    return false;
  }

  private String getDriverDownloadURL(String osName, Browsers browser, String zipFileName) {
    return String.format("http://drivers.testsigma.com/%s/%s/%s", osName, browser.getBrowserFolderName(),
      zipFileName);
  }

  private void unzipDriver(File sourceZipFile, File destinationFolder) throws IOException {
    File destDir = new File(destinationFolder.getAbsolutePath());
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZipFile));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      File newFile = newFile(destDir, zipEntry);
      if (zipEntry.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
          throw new IOException("Failed to create directory " + newFile);
        }
      } else {
        // fix for Windows-created archives
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
          throw new IOException("Failed to create directory " + parent);
        }

        // write file content
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
        if (osType.equalsIgnoreCase("mac") || osType.equalsIgnoreCase("linux")) {
          Runtime.getRuntime().exec("chmod u+x " + newFile.getAbsolutePath());
        }
      }
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
    sourceZipFile.delete();
  }
}
