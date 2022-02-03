package com.testsigma.repository;

import com.testsigma.model.DryTestPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DryTestPlanRepository extends JpaRepository<DryTestPlan, Long> {

  Page<DryTestPlan> findAll(Specification<DryTestPlan> spec, Pageable pageable);

}
