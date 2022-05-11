/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.repository;

import com.testsigma.model.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AttachmentRepository extends PagingAndSortingRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment>, JpaRepository<Attachment, Long> {
  Page<Attachment> findAllByEntityIdAndEntity(Long entityId, String entity, Pageable pageable);

    Optional<Attachment> findByEntityIdAndEntityAndImportedId(Long entityId, String entity, Long id);

  List<Attachment> findAllByName(String name);
}
