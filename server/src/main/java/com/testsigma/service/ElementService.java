/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.event.ElementEvent;
import com.testsigma.event.EventType;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.model.Element;
import com.testsigma.model.ElementScreenName;
import com.testsigma.model.TagType;
import com.testsigma.model.TestCase;
import com.testsigma.repository.ElementRepository;
import com.testsigma.specification.ElementSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.specification.TestCaseSpecificationsBuilder;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.ElementScreenNameRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("elementService")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElementService extends XMLExportService<Element> {
  private final ElementRepository elementRepository;
  private final TestCaseService testCaseService;
  private final TagService tagService;
  private final TestStepService testStepService;
  private final ElementMapper elementMapper;
  private final ElementScreenService screenNameService;
  private final ApplicationEventPublisher applicationEventPublisher;

  public List<Element> findByNameInAndWorkspaceVersionId(List<String> elementNames, Long workspaceVersionId) {
    return elementRepository.findByNameInAndWorkspaceVersionId(elementNames, workspaceVersionId);
  }

  public Element findByNameAndVersionId(String name, Long versionId) {
    return elementRepository.findFirstElementByNameAndWorkspaceVersionId(name,
      versionId);
  }

  public Element find(Long id) throws ResourceNotFoundException {
    return elementRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Element is not found with id: " + id));
  }

  public Page<Element> findAll(Specification<Element> specification, Pageable pageable) {
    return elementRepository.findAll(specification, pageable);
  }

  public List<Element> findAllByWorkspaceVersionId(Long workspaceVersionId) {
    return this.elementRepository.findAllByWorkspaceVersionId(workspaceVersionId);
  }

  public Element create(Element element) {
    element = elementRepository.save(element);
    publishEvent(element, EventType.CREATE);
    return element;
  }

  public Element update(Element element, String oldName) {
    element = elementRepository.save(element);
    if (!oldName.equals(element.getName())) {
      testStepService.updateElementName(oldName, element.getName());
      testStepService.updateAddonElementsName(oldName, element.getName());
    }
    publishEvent(element, EventType.UPDATE);
    return element;
  }

  public void delete(Element element) {
    elementRepository.delete(element);
    publishEvent(element, EventType.DELETE);
  }

  public void bulkDelete(Long[] ids, Long workspaceVersionId) throws Exception {
    Boolean allIdsDeleted = true;
    TestCaseSpecificationsBuilder builder = new TestCaseSpecificationsBuilder();
    for (Long id : ids) {
      List<SearchCriteria> params = new ArrayList<>();
      Element element = this.find(id);
      params.add(new SearchCriteria("element", SearchOperation.EQUALITY, element.getName()));
      params.add(new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, workspaceVersionId));
      builder.setParams(params);
      Specification<TestCase> spec = builder.build();
      Page<TestCase> linkedTestCases = testCaseService.findAll(spec, PageRequest.of(0, 1));
      if (linkedTestCases.getTotalElements() == 0) {
        this.delete(element);
      } else {
        allIdsDeleted = false;
      }
    }
    if (!allIdsDeleted) {
      throw new DataIntegrityViolationException("dataIntegrityViolationException: Failed to delete some of the Elements " +
        "since they are already associated to some Test Cases.");
    }
  }

  public void bulkUpdateScreenNameAndTags(Long[] ids, String screenName, String[] tags) throws ResourceNotFoundException {
    for (Long id : ids) {
      Element element = find(id);
      if (screenName.length() > 0) {
        ElementScreenNameRequest elementScreenNameRequest = new ElementScreenNameRequest();
        elementScreenNameRequest.setName(screenName);
        elementScreenNameRequest.setWorkspaceVersionId(element.getWorkspaceVersionId());
        ElementScreenName elementScreenName = screenNameService.save(elementScreenNameRequest);
        element.setScreenNameId(elementScreenName.getId());
      }
      update(element, element.getName());
      tagService.updateTags(Arrays.asList(tags), TagType.ELEMENT, id);
    }
  }

  public void updateByName(String name, ElementRequest elementRequest) {
    Element element = findByNameAndVersionId(name, elementRequest.getWorkspaceVersionId());
    String oldName = element.getName();
    elementMapper.merge(elementRequest, element);
    update(element, oldName);
  }


  public void publishEvent(Element element, EventType eventType) {
    ElementEvent<Element> event = createEvent(element, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public ElementEvent<Element> createEvent(Element element, EventType eventType) {
    ElementEvent<Element> event = new ElementEvent<>();
    event.setEventData(element);
    event.setEventType(eventType);
    return event;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsElementEnabled()) return;
    log.debug("backup process for element initiated");
    writeXML("elements", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for element completed");
  }

  public Specification<Element> getExportXmlSpecification(BackupDTO backupDTO) {
    SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    ElementSpecificationsBuilder elementSpecificationsBuilder = new ElementSpecificationsBuilder();
    elementSpecificationsBuilder.params = params;
    return elementSpecificationsBuilder.build();
  }

  @Override
  protected List<ElementXMLDTO> mapToXMLDTOList(List<Element> list) {
    return elementMapper.mapElements(list);
  }
}
