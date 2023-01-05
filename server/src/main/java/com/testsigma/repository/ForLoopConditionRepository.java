package com.testsigma.repository;

import com.testsigma.model.ForLoopCondition;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ForLoopConditionRepository extends BaseRepository<ForLoopCondition, Long>  {
    Optional<ForLoopCondition> findByTestStepId(Long testStepId);
    Integer deleteByTestStepId(Long testStepId);
    Optional<ForLoopCondition> findByImportedId(Long importedId);
    Optional<ForLoopCondition> findById(Long testStepId);
    List<ForLoopCondition> findAllByTestCaseId(Long testCaseId);
    List<ForLoopCondition> findAllByTestCaseIdIn(List<Long> testCaseIds);
}