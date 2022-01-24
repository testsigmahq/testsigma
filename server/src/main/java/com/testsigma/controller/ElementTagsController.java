package com.testsigma.controller;

import com.testsigma.mapper.TagMapper;
import com.testsigma.model.TagType;
import com.testsigma.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/element_tags")
public class ElementTagsController extends TagsController {

  @Autowired
  public ElementTagsController(TagMapper mapper, TagService tagService) {
    super(mapper, tagService);
  }

  @Override
  protected TagType getTagType() {
    return TagType.ELEMENT;
  }
}
