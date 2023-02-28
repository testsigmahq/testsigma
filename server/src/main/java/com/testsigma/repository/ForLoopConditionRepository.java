package com.testsigma.repository;

import com.testsigma.model.ForLoopCondition;
import com.testsigma.model.ForLoopConditionType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ForLoopConditionRepository extends BaseRepository<ForLoopCondition, Long>  {

    Optional<ForLoopCondition> findByTestStepId(Long testStepId);

    List<ForLoopCondition> findAllByTestStepIdAndImportedId(Long testStepId, Long importedId);

    void deleteByIdAndType(Long id, ForLoopConditionType type);

    void deleteByTestStepId(Long testStepId);

    Optional<ForLoopCondition> findByImportedId(Long importedId);

    @NotNull Optional<ForLoopCondition> findById(Long id);

    List<ForLoopCondition> findAllByTestCaseId(Long testCaseId);

    List<ForLoopCondition> findAllByTestCaseIdIn(List<Long> testCaseIds);

    Optional<ForLoopCondition> findByTestStepIdAndType(Long testStepId, ForLoopConditionType forLoopConditionType);
}