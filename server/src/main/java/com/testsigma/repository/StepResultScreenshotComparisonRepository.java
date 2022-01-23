/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.StepResultScreenshotComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StepResultScreenshotComparisonRepository extends JpaSpecificationExecutor<StepResultScreenshotComparison>, PagingAndSortingRepository<StepResultScreenshotComparison, Long>, JpaRepository<StepResultScreenshotComparison, Long> {
  List<StepResultScreenshotComparison> findAllByTestCaseResultIdAndSimilarityScoreIsNull(Long testCaseResultId);

  List<StepResultScreenshotComparison> findAllByTestCaseResultIdAndDiffCoordinatesNot(Long testCaseResultId, String diffCorOrdinates);
}
