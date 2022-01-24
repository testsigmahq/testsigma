/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.ElementDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.model.*;
import com.testsigma.service.*;
import com.testsigma.specification.ElementSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.ElementScreenNameRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/elements")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElementsController {
  public final ElementScreenService elementScreenService;
  private final ElementService elementService;
  private final ElementMapper elementMapper;
  private final ElementFilterService elementFilterService;
  private final WorkspaceVersionService versionService;
  private final TestStepService testStepService;
  private final String[] randomNames = new String[]{"Smithers", "Kodos", "Hamburgers", "Sweetums", "Fictional beverages",
    "Daisy", "Flash", "Cletus", "Big Bird", "Cookie Monster", "Vader", "Yoda",
    "Dolemite", "Shaft", "Huggybear", "zarniwoop", "slartibartfast", "frogstar",
    "hotblack", "Lovecraftian gods"
  };

  @RequestMapping(method = RequestMethod.POST)
  public ElementDTO create(@RequestBody @Valid ElementRequest elementRequest) throws SQLException, ResourceNotFoundException, TestsigmaDatabaseException {
    Element element = elementMapper.map(elementRequest);
    element = elementService.create(element);
    return elementMapper.map(element);
  }

  @RequestMapping(path = "/filter/{filterId}", method = RequestMethod.GET)
  public Page<ElementDTO> filter(@PathVariable("filterId") Long filterId, @RequestParam("versionId") Long versionId, @PageableDefault(sort = {"name"}) Pageable pageable) throws ResourceNotFoundException {
    ElementFilter elementFilter = elementFilterService.find(filterId);
    WorkspaceVersion version = versionService.find(versionId);
    ElementSpecificationsBuilder builder = new ElementSpecificationsBuilder();
    Specification<Element> spec = builder.build(elementFilter, version);
    Page<Element> elements = elementService.findAll(spec, pageable);
    List<ElementDTO> elementDTOS = elementMapper.map(elements.getContent());
    return new PageImpl<>(elementDTOS, pageable, elements.getTotalElements());
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<ElementDTO> index(ElementSpecificationsBuilder builder, Pageable pageable) {
    Specification<Element> spec = builder.build();
    Page<Element> elements = elementService.findAll(spec, pageable);
    List<ElementDTO> elementDTOS = elementMapper.map(elements.getContent());
    return new PageImpl<>(elementDTOS, pageable, elements.getTotalElements());
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ElementDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    Element element = elementService.find(id);
    ElementDTO elementDTO = elementMapper.map(element);
    return elementDTO;
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  public ElementDTO update(@PathVariable("id") Long id,
                           @RequestBody ElementRequest elementRequest,
                           @RequestParam(value = "reviewSubmittedFrom", required = false) String reviewSubmittedFrom)
    throws ResourceNotFoundException, TestsigmaDatabaseException, SQLException {
    Element element = elementService.find(id);
    String oldName = element.getName();
    elementMapper.merge(elementRequest, element);
    elementService.update(element, oldName);
    return elementMapper.map(element);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.OK)
  public void delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
    elementService.delete(elementService.find(id));
  }

  @RequestMapping(method = RequestMethod.POST, path = "/bulk")
  public List<ElementDTO> bulkCreate(@RequestBody @Valid List<ElementRequest> elementRequestList) throws TestsigmaException {
    if (elementRequestList.size() > 25)
      throw new TestsigmaException("List is too big to process actual size is ::" + elementRequestList.size() + " and allowed is:: 25");
    List<ElementDTO> list = new ArrayList<>();
    for (ElementRequest elementRequest : elementRequestList) {
      Element element = elementMapper.map(elementRequest);
      if (elementRequest.getScreenNameId() == null) {
        ElementScreenNameRequest elementScreenNameRequest = new ElementScreenNameRequest();
        elementScreenNameRequest.setName(elementRequest.getName());
        elementScreenNameRequest.setWorkspaceVersionId(element.getWorkspaceVersionId());
        ElementScreenName screenName = elementScreenService.save(elementScreenNameRequest);
        element.setScreenNameId(screenName.getId());
      } else
        element.setScreenNameId(elementRequest.getScreenNameId());

      element = createWithRandomNameIfUnique(element, 0);
      if (element != null)
        list.add(elementMapper.map(element));
    }
    return list;
  }

  private Element createWithRandomNameIfUnique(Element element, int iteration) {
    if (iteration > 10)
      return null;
    try {
      element = elementService.create(element);
    } catch (DataIntegrityViolationException ex) {
      log.error(ex.getMessage(), ex);
      if (ex.getCause().getCause().getClass().equals(java.sql.SQLIntegrityConstraintViolationException.class) &&
        ex.getCause().getCause().getMessage().contains("Duplicate entry")) {
        Integer random = new Random().nextInt(randomNames.length - 1);
        element.setName(element.getName() + "-" + randomNames[random]);
        element = createWithRandomNameIfUnique(element, iteration + 1);
      }
    }
    return element;
  }

  @DeleteMapping(value = "/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids, @RequestParam(value = "workspaceVersionId") Long workspaceVersionId) throws Exception {
    elementService.bulkDelete(ids, workspaceVersionId);
  }

  @PutMapping(value = "/bulk_update")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkUpdateScreenNameAndTags(@RequestParam(value = "ids[]") Long[] ids,
                                          @RequestParam(value = "screenName") String screenName,
                                          @RequestBody String[] tags)
    throws Exception {
    elementService.bulkUpdateScreenNameAndTags(ids, screenName, tags);
  }

  @GetMapping(value = "/empty/{id}")
  public @ResponseBody
  Page<ElementDTO> findAllEmptyElementsByTestCaseId(@PathVariable(value = "id") Long id,
                                                    @RequestParam(value = "workspaceVersionId") Long workspaceVersionId) throws ResourceNotFoundException {
    List<TestStep> testSteps = testStepService.findAllByTestCaseId(id);
    List<String> names = new ArrayList<>();
    for (TestStep testStep : testSteps) {
      if (testStep.getElement() != null) {
        if (!names.contains(testStep.getElement()))
          names.add(testStep.getElement());
      }
      if (testStep.getFromElement() != null) {
        if (!names.contains(testStep.getFromElement()))
          names.add(testStep.getFromElement());
      }
      if (testStep.getToElement() != null) {
        if (!names.contains(testStep.getToElement()))
          names.add(testStep.getToElement());
      }
    }
    List<SearchCriteria> params = new ArrayList<>();
    List<String> definitions = new ArrayList<>();
    definitions.add(null);
    definitions.add("");
    params.add(new SearchCriteria("name", SearchOperation.IN, names));
    params.add(new SearchCriteria("locatorValue", SearchOperation.IN, definitions));
    params.add(new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, workspaceVersionId));
    ElementSpecificationsBuilder builder = new ElementSpecificationsBuilder();
    builder.setParams(params);
    Specification<Element> spec = builder.build();
    List<ElementDTO> dtos = elementMapper.map(elementService.findAll(spec, Pageable.unpaged()).getContent());
    if (names.indexOf("element") > -1) {
      params.remove(new SearchCriteria("name", SearchOperation.IN, names));
      params.remove(new SearchCriteria("locatorValue", SearchOperation.IN, definitions));
      params.add(new SearchCriteria("name", SearchOperation.EQUALITY, "element"));
      builder.setParams(params);
      spec = builder.build();
      List<Element> placeholderElement = elementService.findAll(spec, Pageable.unpaged()).getContent();
      if (placeholderElement.size() == 0 || (placeholderElement.size() > 0 && placeholderElement.get(0).getName().isEmpty())) {
        ElementDTO placeholderDTO = new ElementDTO();
        placeholderDTO.setName("element");
        dtos.add(placeholderDTO);
      }
    }

    return new PageImpl<>(dtos, Pageable.unpaged(), dtos.size());
  }

}
