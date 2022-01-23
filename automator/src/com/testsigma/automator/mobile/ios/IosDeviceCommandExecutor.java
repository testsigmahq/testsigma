package com.testsigma.automator.mobile.ios;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.utilities.PathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IosDeviceCommandExecutor {

  public String getIosIdeviceExecutablePath() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return PathUtil.getInstance().getIosPath() + File.separator + "tidevice.exe";
    } else {
      return PathUtil.getInstance().getIosPath() + File.separator + "tidevice";
    }
  }

  public Process runDeviceCommand(String[] subCommand) throws AutomatorException {
    try {
      String[] command = ArrayUtils.addAll(new String[]{getIosIdeviceExecutablePath()}, subCommand);

      log.debug("Running a idevice command - " + Arrays.toString(command));

      ProcessBuilder processBuilder = new ProcessBuilder(command);
      return processBuilder.start();
    } catch (Exception e) {
      throw new AutomatorException(e.getMessage());
    }
  }

  public String getProcessStreamResponse(Process p) throws AutomatorException {
    try {
      String stdOut = IOUtils.toString(p.getInputStream(), StandardCharsets.UTF_8);
      String stdError = IOUtils.toString(p.getErrorStream(), StandardCharsets.UTF_8);
      StringBuilder sb = new StringBuilder();
      sb.append(stdOut);
      sb.append(stdError);
      log.debug("idevice command output - " + sb);
      return sb.toString();
    } catch (Exception e) {
      throw new AutomatorException(e.getMessage());
    }
  }
}
