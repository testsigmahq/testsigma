package com.testsigma.agent.browsers;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.VerRsrc;
import com.sun.jna.platform.win32.Version;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.testsigma.automator.entity.OsBrowserType;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
public class WindowsBrowsers {

  private static final String NOT_FOUND = "NOT_FOUND";

  private final HashMap<OsBrowserType, String> browsersMap;

  public WindowsBrowsers() {
    this.browsersMap = OsBrowserMap.getInstance().getBrowserMap();
  }

  private static String executablePath(String browserExecutablePath) {
    String x86Path = System.getenv("ProgramFiles(X86)") + "\\" + browserExecutablePath;
    String programFiles = System.getenv("ProgramFiles") + "\\" + browserExecutablePath;
    String localAppData = System.getenv("LocalAppData") + "\\" + browserExecutablePath;
    if (Paths.get(x86Path).toFile().exists()) {
      log.info("x86Path path found" + x86Path);
      return x86Path;
    } else if (Paths.get(programFiles).toFile().exists()) {
      log.info("programFiles path found" + x86Path);
      return programFiles;
    } else if (Paths.get(localAppData).toFile().exists()) {
      log.info("localAppData path found" + x86Path);
      return localAppData;
    }
    return null;
  }

  private static String getBrowserVersionFromExe(String browserExecutablePath) {
    String browserVersion = NOT_FOUND;
    browserExecutablePath = executablePath(browserExecutablePath);
    if (browserExecutablePath == null) {
      System.out.println("Failed to determine version of browser " + browserExecutablePath);
      return browserVersion;
    }
    try {
      IntByReference lpdwHandle = new IntByReference();
      lpdwHandle.setValue(0);
      int versionInfoStrSize = Version.INSTANCE.GetFileVersionInfoSize(browserExecutablePath, lpdwHandle);
      Memory pBlock = new Memory(versionInfoStrSize);
      PointerByReference lplpBuffer = new PointerByReference();
      Version.INSTANCE.GetFileVersionInfo(browserExecutablePath, 0, versionInfoStrSize, pBlock);
      Version.INSTANCE.VerQueryValue(pBlock, "\\", lplpBuffer, new IntByReference());

      VerRsrc.VS_FIXEDFILEINFO versionFieldStruct;

      (versionFieldStruct = new VerRsrc.VS_FIXEDFILEINFO(lplpBuffer.getValue())).read();

      int majorVersion = versionFieldStruct.dwFileVersionMS.intValue() >> 16;
      int minorVersion = versionFieldStruct.dwFileVersionMS.intValue() & 0xFFFF;
      int buildVersion = versionFieldStruct.dwFileVersionLS.intValue() >> 16;
      int patchVersion = versionFieldStruct.dwFileVersionLS.intValue() & 0xFFFF;

      browserVersion = majorVersion + "." + minorVersion + "." + buildVersion + "." + patchVersion;
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println("Failed to detect version for browser - " + browserExecutablePath);
    }
    return browserVersion;
  }

  public ArrayList<AgentBrowser> getBrowserList() throws Exception {

    ArrayList<AgentBrowser> browserList = new ArrayList<>();
    int arch = System.getProperty("os.arch").contains("64") ? 64 : 32;

    String nodeValue = (arch == 32) ? "" : "WOW6432Node";

    ArrayList<String> localMachine32RegEntriesList = searchRegistry(
      "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Clients\\StartMenuInternet");
    ArrayList<String> currentUser32RegEntriesList = searchRegistry(
      "HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Clients\\StartMenuInternet");

    ArrayList<String> localMachine64RegEntriesList = searchRegistry(
      "HKEY_LOCAL_MACHINE\\SOFTWARE\\Clients\\StartMenuInternet");
    ArrayList<String> currentUser64RegEntriesList = searchRegistry(
      "HKEY_CURRENT_USER\\SOFTWARE\\Clients\\StartMenuInternet");

    for (Entry<OsBrowserType, String> browserEntry : browsersMap.entrySet()) {
      log.debug("Getting version for windows browser : " + browserEntry.getKey() + " - " + browserEntry.getValue());
      try {
        boolean browserVersionFound = false;

        for (String regEntryBrowser : localMachine32RegEntriesList) {
          log.info("Matching " + regEntryBrowser + " with browser key " + browserEntry.getValue());
          if (regEntryBrowser.contains(browserEntry.getValue())) {
            String version = getBrowserVersion(browserEntry);
            try {
              browserList.add(new AgentBrowser(browserEntry.getKey(), version, 0));
            } catch (Exception e) {
              log.info("Ignoring browser because of bellow error");
              log.error(e, e);
            }
            browserVersionFound = true;
            break;
          }
        }

        if (!browserVersionFound) {
          for (String regEntryBrowser : localMachine64RegEntriesList) {
            log.info("Matching " + regEntryBrowser + " with browser key " + browserEntry.getValue());
            if (regEntryBrowser.contains(browserEntry.getValue())) {
              String version = getBrowserVersion(browserEntry);
              try {
                browserList.add(new AgentBrowser(browserEntry.getKey(), version, 0));
              } catch (Exception e) {
                log.info("Ignoring browser because of bellow error");
                log.error(e, e);
              }
              browserVersionFound = true;
              break;
            }
          }
          if (!browserVersionFound) {
            for (String regEntryBrowser : currentUser32RegEntriesList) {
              log.info("Matching " + regEntryBrowser + " with browser key " + browserEntry.getValue());
              if (regEntryBrowser.contains(browserEntry.getValue())) {
                String version = getVersionFromKey(browserEntry.getValue());
                try {
                  browserList.add(new AgentBrowser(browserEntry.getKey(), version, 0));
                } catch (Exception e) {
                  log.info("Ignoring browser because of bellow error");
                  log.error(e, e);
                }
                browserVersionFound = true;
              }
            }
            if (!browserVersionFound) {
              for (String regEntryBrowser : currentUser64RegEntriesList) {
                log.info("Matching " + regEntryBrowser + " with browser key " + browserEntry.getValue());
                if (regEntryBrowser.contains(browserEntry.getValue())) {
                  String version = getVersionFromKey(browserEntry.getValue());
                  try {
                    browserList.add(new AgentBrowser(browserEntry.getKey(), version, 0));
                  } catch (Exception e) {
                    log.info("Ignoring browser because of bellow error");
                    log.error(e, e);
                  }
                }
              }
            }
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    return browserList;
  }

  private String getBrowserVersion(Map.Entry<OsBrowserType, String> browserEntry) {
    String version = NOT_FOUND;

    int browserType = browserEntry.getKey().getValue();
    log.debug("Getting windows browser version for browser type - " + browserType);
    switch (browserType) {
      case 1:
        version = getChromeVersion();
        break;
      case 2:
        version = getFirefoxVersion();
        break;
      case 4:
        version = getEdgeVersion();
        break;
    }
    log.debug("Found browser " + browserEntry.getKey().name() + " with version - " + version);
    return version;
  }

  private String getEdgeVersion() {
    String version = NOT_FOUND;
    String edgeRegKey = searchRegistryByPattern(
      "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\",
      ".*(Microsoft Edge$)");

    if (StringUtils.isNotBlank(edgeRegKey)) {
      version = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + edgeRegKey,
        ".*Version .* (.*)");
    }

    if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
      edgeRegKey = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\",
        ".*(Microsoft Edge$)");

      if (StringUtils.isNotBlank(edgeRegKey)) {
        version = searchRegistryByPattern(
          "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
            + edgeRegKey,
          ".*Version .* (.*)");
      }
    }

    if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
      String registryKey = "HKEY_CLASSES_ROOT\\Local Settings\\Software\\Microsoft\\Windows\\CurrentVersion\\AppModel\\PackageRepository\\Packages";
      String regexPattern = ".*Microsoft.MicrosoftEdge.Stable_(.*)";
      String regValue = searchRegistryByPattern(registryKey, regexPattern);
      version = regValue.split("_")[0];
      if(StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
        regexPattern = ".*Microsoft.MicrosoftEdge_(.*)";
        regValue = searchRegistryByPattern(registryKey, regexPattern);
        version = regValue.split("_")[0];
      }
    }
    if (version.equals(NOT_FOUND))
      version = getBrowserVersionFromExe("Microsoft\\Edge\\Application\\msedge.exe");
    return version;
  }

  private String getChromeVersion() {
    String version = NOT_FOUND;
    String chromeRegKey = searchRegistryByPattern(
      "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\",
      ".*(Google Chrome.*)");

    if (StringUtils.isNotBlank(chromeRegKey)) {
      version = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + chromeRegKey,
        ".*Version .* (.*)");
    }

    if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
      chromeRegKey = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\",
        ".*(Google Chrome.*)");

      if (StringUtils.isNotBlank(chromeRegKey)) {
        version = searchRegistryByPattern(
          "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
            + chromeRegKey,
          ".*Version .* (.*)");
      }
    }
    if (version.equals(NOT_FOUND))
      version = getBrowserVersionFromExe("Google\\Chrome\\Application\\chrome.exe");
    return version;
  }

  private String getFirefoxVersion() {
    String version = NOT_FOUND;
    String firefoxRegKey = searchRegistryByPattern(
      "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\",
      ".*(Mozilla Firefox.*)");
    if (StringUtils.isNotBlank(firefoxRegKey)) {
      version = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + firefoxRegKey,
        ".*Version .* (.*)");
    }

    if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
      firefoxRegKey = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\",
        ".*(Mozilla Firefox.*)");
      if (StringUtils.isNotBlank(firefoxRegKey)) {
        version = searchRegistryByPattern(
          "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\"
            + firefoxRegKey,
          ".*Version .* (.*)");
      }
    }
    if (version.equals(NOT_FOUND))
      version = getBrowserVersionFromExe("Mozilla Firefox\\firefox.exe");
    return version;
  }

  private String searchRegistryByPattern(String registryKey, String regexPattern) {
    String result = NOT_FOUND;
    Pattern pattern = Pattern.compile(regexPattern);

    ArrayList<String> regEntriesList = searchRegistry(registryKey);

    for (String regKey : regEntriesList) {
      Matcher matcher = pattern.matcher(regKey);

      if (regKey.matches(pattern.pattern()) && matcher.find()) {
        result = matcher.group(1);
        break;
      }
    }
    return result;
  }

  public ArrayList<String> searchRegistry(String key) {
    Process process;
    String line;
    ArrayList<String> regEntriesList = new ArrayList<String>();
    ProcessBuilder processBuilder = new ProcessBuilder("reg", "query", key);

    try {
      process = processBuilder.start();
      BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
      while ((line = bufferedReader.readLine()) != null) {
        regEntriesList.add(line);
      }
    } catch (Exception exception) {
      log.error("Error while fetching browser list - " + exception.getMessage(), exception);
      exception.printStackTrace();
    }
    return regEntriesList;
  }

  private String getVersionFromKey(String key) {
    String version = searchRegistryByPattern(
      "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key,
      ".*Version .* (.*)");
    log.info("Matched HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key
      + " and found version - " + version);
    if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
      version = searchRegistryByPattern(
        "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key,
        ".*Version .* (.*)");
      log.info("Matched HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key
        + " and found version - " + version);
      if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
        version = searchRegistryByPattern(
          "HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key,
          ".*Version .* (.*)");
        log.info("Matched HKEY_CURRENT_USER\\SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key
          + " and found version - " + version);
        if (StringUtils.isBlank(version) || NOT_FOUND.equals(version)) {
          version = searchRegistryByPattern(
            "HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key,
            ".*Version .* (.*)");
          log.info("Matched HKEY_CURRENT_USER\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" + key
            + " and found version - " + version);
        }
      }
    }
    return version;
  }
}
