package com.testsigma.controller;

import com.testsigma.mapper.TagMapper;
import com.testsigma.model.TagType;
import com.testsigma.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test_plan_tags")
public class TestPlanTagsController extends TagsController {

    @Autowired
    public TestPlanTagsController(TagMapper mapper, TagService tagService) {
        super(mapper, tagService);
    }

    @Override
    protected TagType getTagType() {
        return TagType.TEST_PLAN;
    }
}
