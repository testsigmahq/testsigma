/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.mapper;

import com.testsigma.dto.TagDTO;
import com.testsigma.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
  TagDTO map(Tag tagModel);

  List<TagDTO> map(List<Tag> list);

}
