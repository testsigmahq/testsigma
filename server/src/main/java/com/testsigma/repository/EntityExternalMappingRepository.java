/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.EntityExternalMapping;
import com.testsigma.model.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface EntityExternalMappingRepository extends JpaRepository<EntityExternalMapping, Long>, JpaSpecificationExecutor<EntityExternalMapping> {
  Optional<EntityExternalMapping> findByEntityIdAndEntityTypeAndApplicationId(Long entityId, EntityType entityType, Long applicationId);
  List<EntityExternalMapping> findByExternalIdAndEntityTypeAndApplicationId(String externalId, EntityType entityType, Long applicationId);
  Optional<EntityExternalMapping> findByEntityIdAndEntityType(String entityId, EntityType entityType);
  List<EntityExternalMapping> findAllByEntityIdAndEntityType(Long entityId, EntityType entityType);

  @Query("SELECT eem FROM EntityExternalMapping eem where eem.entityId in :ids " +
          "AND eem.entityType = :entityType AND eem.applicationId = :workspaceId")
  List<EntityExternalMapping> findByEntityIds(Long[] ids, EntityType entityType, Long workspaceId);

  @Modifying
  @Query("DELETE FROM EntityExternalMapping mapping WHERE mapping.id IN (:ids)")
  void deleteAllByIds(@Param("ids") Set<Long> ids);
}
