package com.testsigma.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestDataParameterUpdateTaskHandler {
  private ExecutorService executorService;

  @PostConstruct
  private void initializeThreadPool() {
    executorService = Executors.newSingleThreadExecutor();
  }

  @PreDestroy
  private void shutdownThreadPool() {
    executorService.shutdownNow();
  }

  public void startTask(Runnable testDataParameterUpdateTask) {
    executorService.submit(testDataParameterUpdateTask);
  }

}
