package com.testsigma.agent.browsers;

import com.testsigma.automator.entity.OsBrowserType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Component
public class LinuxBrowsers {

  private final HashMap<OsBrowserType, String> browsersMap;

  public LinuxBrowsers() {
    this.browsersMap = OsBrowserMap.getInstance().getBrowserMap();
  }

  public static ArrayList<String> getCommandOutput(String[] command) {
    Process process;
    ArrayList<String> arrayList = new ArrayList<>();

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      log.debug("Executing command - " + Arrays.toString(command));

      processBuilder.environment().put("PATH", System.getenv("PATH"));

      process = processBuilder.start();

      BufferedReader bufferedReader = new BufferedReader(
        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

      String str;
      while ((str = bufferedReader.readLine()) != null) {
        arrayList.add(str);
      }

      bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
      while ((str = bufferedReader.readLine()) != null) {
        arrayList.add(str);
      }
    } catch (Exception exception) {
      log.error("Error while fetching browser list - " + exception.getMessage(), exception);
    }
    log.debug("Response from the command - " + Arrays.toString(arrayList.toArray()));
    return arrayList;

  }

  public ArrayList<AgentBrowser> getBrowserList() {

    ArrayList<AgentBrowser> browserList = new ArrayList<>();
    for (OsBrowserType browserType : browsersMap.keySet()) {
      String browserName = browsersMap.get(browserType);
      ArrayList<String> arrayList = getCommandOutput(new String[]{"which", browserName});

      if (arrayList.size() > 0) {

        if (!arrayList.get(0)
          .contains("which: no " + browserName + " in")) {

          String version = "";
          ArrayList<String> versionOutput;
          log.debug("Fetching browser version for " + browserType.name() + " with ordinal: " + browserType.getValue());
          switch (browserType.getValue()) {
            case 1:
              log.debug("Fetching linux browsers version....chrome version");
              versionOutput = getCommandOutput(new String[]{arrayList.get(0), "--product-version"});
              version = versionOutput.get(0);
              break;
            case 2:
              log.debug("Fetching linux browsers version....firefox version");
              versionOutput = getCommandOutput(new String[]{arrayList.get(0), "-v"});
              Pattern pattern = Pattern.compile("^[\\D\\s]*([0-9.]+)[\\D\\s]*$");
              Matcher matcher = pattern.matcher(versionOutput.get(0));
              if (matcher.find()) {
                version = matcher.group(1);
              }
              break;
          }
          log.debug("Found version - " + version);
          try {
            browserList.add(new AgentBrowser(browserType, version, 0));
          } catch (Exception e) {
            log.info("Ignoring browser because of bellow error");
            log.error(e, e);
          }
        }
      }
    }
    return browserList;
  }

}
