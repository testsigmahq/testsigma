/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.service;

import com.testsigma.model.Tag;
import com.testsigma.model.TagType;
import com.testsigma.model.TagEntityMapping;
import com.testsigma.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "tagService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TagService {

  private final TagRepository tagRepository;
  private final TagEntityMappingService tagEntityMappingService;

  public List<Tag> list(TagType type) {
    return tagRepository.findAllByType(type);
  }

  public void updateTags(List<String> tagNames, TagType type, Long entityId) {

    removedUnused(tagNames, type, entityId);
    saveTags(tagNames, type, entityId);
    tagRepository.updateUsageCount();
  }

  private void removedUnused(List<String> tagNames, TagType type, Long entityId) {
    List<Tag> list = tagRepository.findAllByTagUses(type.name(), entityId);
    Map<String, Long> removed = new HashMap<String, Long>();
    for (Tag tag : list) {
      if (tagNames.indexOf(tag.getName()) == -1) {
        removed.put(tag.getName(), tag.getId());
        tagNames.remove(tag.getName());
      }
    }
    if (removed.size() > 0) {
      List<TagEntityMapping> tagUses = tagEntityMappingService.findAllByTagIdInAndTypeAndEntityId(new ArrayList<>(removed.values()), type, entityId);
      this.tagEntityMappingService.deleteAll(tagUses);
    }
  }

  private void saveTags(List<String> tagNames,
                        TagType type, Long entityId) {

    List<Tag> tags = tagRepository.findAllByType(type);
    List<String> savedTags = tags.stream().map(Tag::getName).collect(Collectors.toList());

    List<TagEntityMapping> newTagUses = new ArrayList<TagEntityMapping>();
    Map<String, Long> allNames = tags.stream().
      collect(Collectors.toMap(Tag::getName, Tag::getId));

    tagNames = (tagNames != null) ? tagNames : new ArrayList<>();

    List<TagEntityMapping> existing = tagEntityMappingService.findAllByTypeAndEntityId(type, entityId);
    List<Long> existingTagIds = existing.stream().map(TagEntityMapping::getTagId).collect(Collectors.toList());

    for (String tagName : tagNames) {
      Long tagId = allNames.get(tagName);
      if (savedTags.indexOf(tagName) == -1) {
        tagId = saveTag(tagName, type);
      }
      if (!existingTagIds.contains(tagId)) {
        TagEntityMapping tagEntityMapping = new TagEntityMapping();
        tagEntityMapping.setTagId(tagId);
        tagEntityMapping.setType(type);
        tagEntityMapping.setEntityId(entityId);
        newTagUses.add(tagEntityMapping);
      }
    }
    if (newTagUses.size() > 0) {
      this.tagEntityMappingService.createAll(newTagUses);
    }
  }

  private Long saveTag(String tagName, TagType type) {

    Tag tag = new Tag();
    tag.setName(tagName);
    tag.setType(type);
    tag.setCount(1);
    tag.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    tag = tagRepository.save(tag);

    return tag.getId();
  }

  public List<String> list(TagType type, Long entityId) {

    List<Tag> list = tagRepository.findAllByTagUses(type.name(), entityId);
    list = (list == null) ? new ArrayList<>() : list;
    return list.stream().map(Tag::getName).collect(Collectors.toList());
  }

  public List<Tag> assignedLst(TagType type, Long entityId) {

    return tagRepository.findAllByTagUses(type.name(), entityId);

  }
}
