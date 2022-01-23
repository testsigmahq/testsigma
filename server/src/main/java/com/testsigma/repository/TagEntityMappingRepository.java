/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.TagType;
import com.testsigma.model.TagEntityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public interface TagEntityMappingRepository extends JpaRepository<TagEntityMapping, Long> {

  List<TagEntityMapping> findAllByTypeAndEntityId(TagType type, Long entityId);

  List<TagEntityMapping> findAllByTagIdInAndTypeAndEntityId(Collection<Long> tagId, TagType type, Long entityId);
}
