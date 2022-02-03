package com.testsigma.repository;

import com.testsigma.model.MobileInspection;
import com.testsigma.model.MobileInspectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public interface MobileInspectionRepository extends JpaRepository<MobileInspection, Long> {

  Page<MobileInspection> findAll(Specification<MobileInspection> spec, Pageable pageable);

  List<MobileInspection> findAllByLastActiveAtBeforeAndStatusIn(Timestamp lastActiveAt, Collection<MobileInspectionStatus> statusTypes);

}
