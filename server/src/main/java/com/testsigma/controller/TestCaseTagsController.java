package com.testsigma.controller;

import com.testsigma.mapper.TagMapper;
import com.testsigma.model.TagType;
import com.testsigma.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/testcase_tags")
public class TestCaseTagsController extends TagsController {

  @Autowired
  public TestCaseTagsController(TagMapper mapper, TagService tagService) {
    super(mapper, tagService);
  }

  @Override
  protected TagType getTagType() {
    return TagType.TEST_CASE;
  }
}
