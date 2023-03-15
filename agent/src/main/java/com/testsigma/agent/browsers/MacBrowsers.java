package com.testsigma.agent.browsers;

import com.dd.plist.NSObject;
import com.testsigma.agent.utils.PathUtil;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.testsigma.automator.entity.OsBrowserType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class MacBrowsers {

  private final Path applicationsListFilePath;
  private final HashMap<OsBrowserType, String> browsersMap;

  public MacBrowsers() {
    String workingDirectory = PathUtil.getInstance().getTempPath();
    File f = new File(workingDirectory);
    if (!f.exists()) {
      f.mkdir();
    }
    this.applicationsListFilePath = Paths.get(workingDirectory, "Applications.plist");
    this.browsersMap = OsBrowserMap.getInstance().getBrowserMap();
  }

  public Set<AgentBrowser> getBrowserList() {
    Set<AgentBrowser> browserList = new HashSet<>();

    try {
      if (runApplicationListCommand()) {
        NSArray applications = (NSArray) ((NSDictionary) ((NSArray) PropertyListParser
          .parse(applicationsListFilePath.toFile())).objectAtIndex(0)).get("_items");

        for (int i = 0; i < applications.count(); i++) {
          NSDictionary application = (NSDictionary) applications.objectAtIndex(i);
          String applicationName = application.get("_name").toJavaObject().toString();

          if (browsersMap.containsValue(applicationName)) {
            log.info("Matched application name as browser - " + applicationName);
            try {
              browserList.add(getAgentBrowser(application));
            } catch (Exception e) {
              log.info("Ignoring browser ( " + applicationName + " )because of bellow error");
              log.error(e.getMessage(), e);
            }
          }
        }

      }
    } catch (Exception exception) {
      log.error("Error while fetching list of installed browsers - " + exception.getMessage(), exception);
    } finally {
      File f = applicationsListFilePath.toFile();
      if (f.exists())
        f.delete();
    }

    return browserList;
  }

  private AgentBrowser getAgentBrowser(NSDictionary application) {
    log.debug("retrieving agent browser details from dictionary object...");
    String applicationName = application.get("_name").toJavaObject().toString();
    NSObject versionObj = application.get("version");
    String version = "0";
    if(versionObj != null) {
      version = versionObj.toJavaObject().toString();
    }
    log.debug("Found browser " + applicationName + " with version - " + version);
    return new AgentBrowser(getBrowserType(applicationName), version, 64);
  }

  private OsBrowserType getBrowserType(String name) {
    for (OsBrowserType key : browsersMap.keySet()) {
      if (browsersMap.get(key).equals(name)) {
        return key;
      }
    }
    return OsBrowserType.Unknown;
  }

  private boolean runApplicationListCommand() throws Exception {
    String workingDirectory = PathUtil.getInstance().getTempPath();
    Path applicationsListFilePath = Paths.get(workingDirectory, "Applications.plist");

    boolean processFinished;
    ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c",
      "system_profiler SPApplicationsDataType -xml > \"" + applicationsListFilePath.toAbsolutePath() + "\"");

    processBuilder.directory(new File(workingDirectory));

    processFinished = processBuilder.start().waitFor(65, TimeUnit.SECONDS);

    return processFinished;
  }
}
