package com.testsigma.service;

import com.testsigma.exception.TestsigmaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProcessService {
  public void runCommand(String[] command) throws TestsigmaException {
    try {
      log.info("Running a process command - " + Arrays.toString(command));

      ProcessBuilder processBuilder = new ProcessBuilder(command);
      Process p = processBuilder.start();

      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null)
        sb.append(line);

      log.info(sb.toString());

      br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      sb = new StringBuilder();
      while ((line = br.readLine()) != null)
        sb.append(line);

      log.info(sb.toString());

    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage());
    }
  }
}
