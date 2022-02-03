package com.testsigma.repository;

import com.testsigma.model.TestPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TestPlanRepository extends JpaRepository<TestPlan, Long> {
  Page<TestPlan> findAll(Specification<TestPlan> spec, Pageable pageable);

  List<TestPlan> findAllByWorkspaceVersionId(Long versionId);
}
