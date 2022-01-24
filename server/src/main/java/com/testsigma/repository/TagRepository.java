/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.Tag;
import com.testsigma.model.TagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TagRepository extends JpaRepository<Tag, Long> {
  List<Tag> findAllByType(TagType type);

  @Query(value = "SELECT tags.* FROM tags JOIN tag_entity_mapping tu ON tu.tag_id=tags.id WHERE tu.type= :tagUseType AND tu" +
    ".entity_id= :tagUseTypeId", nativeQuery = true)
  List<Tag> findAllByTagUses(@Param("tagUseType") String tagUseType, @Param("tagUseTypeId") Long tagUseTypeId);

  @Modifying
  @Query(value = "UPDATE tags tg SET count = (SELECT COUNT(tag_id) FROM tag_entity_mapping tu where tu.tag_id=tg.id)",
    nativeQuery = true)
  Integer updateUsageCount();
}
