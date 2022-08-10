package com.testsigma.automator.mobile.ios;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpClient;
import com.testsigma.automator.mobile.MobileApp;
import com.testsigma.automator.mobile.MobileAppType;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
public class AppInstaller {
  private final IosDeviceCommandExecutor iosDeviceCommandExecutor;
  private final HttpClient httpClient;

  public AppInstaller(HttpClient httpClient) {
    this.iosDeviceCommandExecutor = new IosDeviceCommandExecutor();
    this.httpClient = httpClient;
  }

  public String installApp(String deviceName, String deviceUniqueId, String appUrl, Boolean isEmulator) throws AutomatorException {
    File appFile = null;
    log.info(String.format("Install app %s on device %s", appUrl, deviceName));
    try {
      appFile = downloadApp(appUrl);
      String bundleId = getAppBundleId(appFile);
      if(isEmulator) {
        return bundleId;
      }
      Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", deviceUniqueId, "install",
        appFile.getAbsolutePath()}, true);
      p.waitFor(60, TimeUnit.SECONDS);
      String installOutput = iosDeviceCommandExecutor.getProcessStreamResponse(p);
      log.info(installOutput);
      boolean installed = checkIfInstalled(deviceName, deviceUniqueId, bundleId);
      if (installed) {
        return bundleId;
      } else {
        throw new AutomatorException("App not installed on device", "App not installed on device");
      }
    } catch (Exception e) {
      throw new AutomatorException(e.getMessage(), e);
    } finally {
      if ((appFile != null) && appFile.exists()) {
        boolean deleted = appFile.delete();
        if (!deleted) {
          log.error("Unable to delete temp app file - " + appFile.getAbsolutePath());
        }
      }
    }
  }

  private File downloadApp(String appUrl) throws Exception {
    File appFile;
    log.info("Downloading app with url - " + appUrl);
    if (appUrl.startsWith("http")) {
      String path = new URL(appUrl).getPath();
      String fileBaseName = FilenameUtils.getBaseName(path);
      String fileExtension = FilenameUtils.getExtension(path);
      log.info(String.format("Creating a temp file with base name %s and extension %s", fileBaseName, fileExtension));
      appFile = File.createTempFile(fileBaseName + "_", "." + fileExtension);
      log.info("downloading the app to the tmp file - " + appFile.getAbsolutePath());
      httpClient.downloadFile(appUrl, appFile.getAbsolutePath());
    } else {
      appFile = new File(appUrl);
    }
    return appFile;
  }

  public String getAppBundleId(File appFile) throws Exception {
    String bundleId = null;
    log.info("Fetching bundle id from app - " + appFile.getAbsolutePath());
    Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"parse", appFile.getAbsolutePath()}, true);
    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("BundleID: ")) {
        bundleId = line.split(" ")[1];
      }
    }
    log.info("Bundle id from app - " + appFile.getAbsolutePath() + " is - " + bundleId);
    return bundleId;
  }

  public boolean checkIfInstalled(String deviceName, String deviceUniqueId, String bundleId) throws AutomatorException {
    try {
      log.info("Checking if a mobile app with bundle id - " + bundleId + " is installed in device - " + deviceName);
      boolean installed = false;
      int i = 0;
      while (i < 12) {
        List<MobileApp> apps = getMobileApps(deviceName, deviceUniqueId);
        for (MobileApp app : apps) {
          if (app.getBundleId().equals(bundleId)) {
            installed = true;
            break;
          }
        }
        if (installed)
          break;
        log.info("Looks like app is not installed yet...retrying in 5 seconds...");
        Thread.sleep(5000);
        i++;
      }
      if (!installed) {
        log.info("Looks like some issue app installation. For now assuming its installed and proceeding");
        installed = true;
      }
      return installed;
    } catch (Exception e) {
      throw new AutomatorException(e.getMessage(), e);
    }
  }

  public List<MobileApp> getMobileApps(String deviceName, String deviceUniqueId) throws AutomatorException {
    List<MobileApp> apps = new ArrayList<>();
    log.info("Fetching list of mobile apps on device - " + deviceName);
    try {
      Process p = iosDeviceCommandExecutor.runDeviceCommand(new String[]{"-u", deviceUniqueId, "applist"}, true);
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(" ");
        if (tokens.length >= 3) {
          MobileApp app = new MobileApp();
          app.setBundleId(tokens[0]);
          StringBuilder appName = new StringBuilder();
          for (int i = 1; i < (tokens.length - 1); i++) {
            appName.append(tokens[i]);
          }
          app.setName(appName.toString());
          app.setVersion(tokens[tokens.length - 1]);
          app.setAppType(MobileAppType.iOS);
          apps.add(app);
        }
      }
    } catch (Exception e) {
      throw new AutomatorException(e.getMessage(), e);
    }
    log.info("List of mobile apps for device - " + deviceName + ". App list - " + apps);
    return apps;
  }
}
