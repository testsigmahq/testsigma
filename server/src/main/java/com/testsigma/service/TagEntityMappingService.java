/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.service;

import com.testsigma.model.TagType;
import com.testsigma.model.TagEntityMapping;
import com.testsigma.repository.TagEntityMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagEntityMappingService {

  private final TagEntityMappingRepository tagEntityMappingRepository;

  public void deleteAll(List<TagEntityMapping> tagUses) {
    this.tagEntityMappingRepository.deleteAll(tagUses);
  }

  public List<TagEntityMapping> createAll(List<TagEntityMapping> tagUses) {
    return tagEntityMappingRepository.saveAll(tagUses);
  }

  public List<TagEntityMapping> findAllByTagIdInAndTypeAndEntityId(List<Long> tagIds, TagType type, Long entityId) {
    return tagEntityMappingRepository.findAllByTagIdInAndTypeAndEntityId(tagIds, type, entityId);
  }

  public List<TagEntityMapping> findAllByTypeAndEntityId(TagType type, Long entityId) {
    return tagEntityMappingRepository.findAllByTypeAndEntityId(type, entityId);
  }
}
