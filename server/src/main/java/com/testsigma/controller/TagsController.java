package com.testsigma.controller;

import com.testsigma.dto.TagDTO;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TagMapper;
import com.testsigma.model.TagType;
import com.testsigma.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class TagsController {

  private final TagMapper mapper;
  private final TagService tagService;

  @RequestMapping(method = RequestMethod.GET)
  public List<TagDTO> index() throws TestsigmaException {
    return mapper.map(tagService.list(getTagType()));
  }

  @RequestMapping(path = "/associate_item/{id}", method = RequestMethod.POST)
  public HttpStatus save(@PathVariable("id") Long id, @RequestBody List<String> tags)
    throws TestsigmaException {
    tagService.updateTags(tags, getTagType(), id);
    return HttpStatus.OK;
  }

  @RequestMapping(path = "/associate_item/{id}", method = RequestMethod.GET)
  public List<TagDTO> index(@PathVariable("id") Long id) throws TestsigmaException {
    return mapper.map(tagService.assignedLst(getTagType(), id));
  }

  @ExceptionHandler({TestsigmaDatabaseException.class, TestsigmaException.class})
  public ResponseEntity<Object> handleTagNotFoundException(Exception ex) {

    return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.OK);
  }

  protected abstract TagType getTagType();
}
