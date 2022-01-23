package com.testsigma.service;

import com.testsigma.tasks.ReSignTask;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Log4j2
@Data
public class ReSignTaskFactory {
  private static ReSignTaskFactory _instance = null;
  private ExecutorService executorService;
  private Map<Long, Future<ReSignTask>> tasksMap;
  private Map<Long, Timestamp> taskStartTimerMap;

  public static ReSignTaskFactory getInstance() {
    return _instance;
  }

  public void startTask(ReSignTask reSignTask) {
    Future<ReSignTask> future = executorService.submit(reSignTask, reSignTask);
    tasksMap.put(reSignTask.getId(), future);
    taskStartTimerMap.put(reSignTask.getId(), new Timestamp(System.currentTimeMillis()));
  }

  @PostConstruct
  private void initializeThreadPool() {
    executorService = Executors.newScheduledThreadPool(5);
    tasksMap = new ConcurrentHashMap<>();
    taskStartTimerMap = new ConcurrentHashMap<>();
    _instance = this;
  }

  @PreDestroy
  private void shutdownThreadPool() {
    executorService.shutdownNow();
    _instance = null;
  }

  @Scheduled(cron = "5 * * * * *")
  private void cancelExpiredTasks() {
    log.debug("Checking for any expired Tenant ReSignUpload tasks....");
    taskStartTimerMap.forEach((id, time) -> {
      Future<ReSignTask> future = tasksMap.get(id);
      if (future == null) {
        tasksMap.remove(id);
        taskStartTimerMap.remove(id);
      } else {
        boolean taskTimerExpired = ((new Timestamp(System.currentTimeMillis()).getTime() - time.getTime()) > 60 * 60 * 1000);
        if (future.isDone()) {
          tasksMap.remove(id);
          taskStartTimerMap.remove(id);
        } else if (taskTimerExpired) {
          log.info("Found Tenant (" + id + ") ReSign task still in progress after grace time period. Cancelling it");
          future.cancel(true);
          tasksMap.remove(id);
          taskStartTimerMap.remove(id);
        }
      }
    });
  }
}
