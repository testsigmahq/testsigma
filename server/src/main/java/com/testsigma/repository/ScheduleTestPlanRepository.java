/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.ScheduleTestPlan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public interface ScheduleTestPlanRepository extends BaseRepository<ScheduleTestPlan, Long> {

  @Query(value = "SELECT DISTINCT schedule FROM ScheduleTestPlan schedule " +
    "LEFT OUTER JOIN TestPlanResult result on result.testPlanId = schedule.testPlanId  " +
    "WHERE ((result.status IN(com.testsigma.model.StatusConstant.STATUS_COMPLETED)) OR result.status IS NULL)" +
    "AND schedule.scheduleTime <=:time AND schedule.status = com.testsigma.model.ScheduleStatus.ACTIVE AND schedule.queueStatus = com.testsigma.model.ScheduleQueueStatus.COMPLETED")
  List<ScheduleTestPlan> findAllActiveScheduleTestPlans(@Param("time") Timestamp time);

}

