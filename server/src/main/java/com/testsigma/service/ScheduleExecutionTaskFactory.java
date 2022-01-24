package com.testsigma.service;


import com.testsigma.schedulers.scheduleTestPlanTask;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Log4j2
@Data
public class ScheduleExecutionTaskFactory {
  private static ScheduleExecutionTaskFactory _instance = null;
  private ExecutorService executorService;
  private ArrayList<Future<scheduleTestPlanTask>> tasksMap;
  private ArrayList<Timestamp> taskStartTimerMap;

  public static ScheduleExecutionTaskFactory getInstance() {
    return _instance;
  }

  public void startTask(scheduleTestPlanTask scheduleTestPlanTask) {
    Future<scheduleTestPlanTask> future = (Future<scheduleTestPlanTask>)
      executorService.submit(scheduleTestPlanTask);
    tasksMap.add(future);
    taskStartTimerMap.add(new Timestamp(System.currentTimeMillis()));
  }


  @PostConstruct
  private void initializeThreadPool() {
    executorService = Executors.newCachedThreadPool();
    tasksMap = new ArrayList<>();
    taskStartTimerMap = new ArrayList<>();
    _instance = this;
  }

  @PreDestroy
  private void shutdownThreadPool() {
    executorService.shutdownNow();
    _instance = null;
  }

  @Scheduled(cron = "5 * * * * *")
  private void cancelExpiredTasks() {
    log.debug("Checking for any expired  Schedule Execution tasks....");
    Iterator<Timestamp> taskStartTimerMapIterator = taskStartTimerMap.iterator();
    while (taskStartTimerMapIterator.hasNext()) {
      Timestamp timestamp = taskStartTimerMapIterator.next();
      if (timestamp == null) {
        taskStartTimerMapIterator.remove();
      } else {
        Future<scheduleTestPlanTask> future = tasksMap.get(taskStartTimerMap.indexOf(timestamp));
        boolean taskTimerExpired = ((new Timestamp(System.currentTimeMillis()).getTime() - timestamp.getTime()) > 60 * 60 * 1000);
        if (future.isDone()) {
          taskStartTimerMapIterator.remove();
        } else if (taskTimerExpired) {
          future.cancel(true);
          taskStartTimerMapIterator.remove();
        }
      }
    }
  }
}
