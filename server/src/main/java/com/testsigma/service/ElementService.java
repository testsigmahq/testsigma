/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.ElementCloudXMLDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.event.ElementEvent;
import com.testsigma.event.EventType;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.model.*;
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
import java.util.*;

@Service("elementService")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ElementService extends XMLExportImportService<Element> {
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

  public Element create(Element element) {
    element = this.save(element);
    this.markAsDuplicated(element);
    publishEvent(element, EventType.CREATE);
    return element;
  }

  public Element update(Element element, String oldName, String previousLocatorValue, LocatorType previousLocatorType, Long previousScreenNameId)
          throws ResourceNotFoundException {
    element = this.save(element);
    if (!Objects.equals(element.getLocatorValue(), previousLocatorValue) || element.getLocatorType() != previousLocatorType
            || !Objects.equals(element.getScreenNameId(), previousScreenNameId)) {
      this.markAsDuplicated(element);
      this.resetDuplicate(element.getWorkspaceVersionId(), previousLocatorValue, previousLocatorType, previousScreenNameId);
    }
    this.eventCallForUpdate(oldName, element);
    return element;
  }

  public Element update(Element element, String oldName) {
    element = this.save(element);
    this.eventCallForUpdate(oldName, element);
    return element;
  }

  public void delete(Element element) {
    elementRepository.delete(element);
    this.resetDuplicate(element.getWorkspaceVersionId(), element.getLocatorValue(), element.getLocatorType(), element.getScreenNameId());
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
      update(element, element.getName(), element.getLocatorValue(), element.getLocatorType(), element.getScreenNameId());
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

  private void eventCallForUpdate(String oldName, Element element){
    if (!oldName.equals(element.getName())) {
      testStepService.updateElementName(oldName, element.getName());
      testStepService.updateAddonElementsName(oldName, element.getName());
    }
    publishEvent(element, EventType.UPDATE);
  }

  public List<Element> findAllMatchedElements(Long applicationVersionId, String definition,
                                              LocatorType locatorType, Long screenNameId, Boolean duplicatedStatus) {
    return this.elementRepository.findAllMatchedElements(applicationVersionId, definition, locatorType, screenNameId, duplicatedStatus);
  }

  public List<Element> findAllMatchedElements(Long applicationVersionId, String definition,
                                              LocatorType locatorType, Long screenNameId) {
    return this.elementRepository.findAllMatchedElements(applicationVersionId, definition, locatorType, screenNameId);
  }


  private void markAsDuplicated(Element element) {
    List<Element> matchedElements = this.findAllMatchedElements
            (element.getWorkspaceVersionId(), element.getLocatorValue(), element.getLocatorType(),
                    element.getScreenNameId());
    if(matchedElements.size() == 1){
      this.resetOnUpdateIfEligible(matchedElements.get(0));
      return;
    }

    matchedElements.forEach(elem -> {
      if(elem.getIsDuplicated())
        return;
      elem.setIsDuplicated(true);
      this.save(elem);
    });
  }

  private void resetDuplicate(Long versionId, String previousLocatorValue, LocatorType previousLocatorType, Long previousScreenId) {
    List<Element> matchedDuplicatedElements = this.findAllMatchedElements
            (versionId, previousLocatorValue, previousLocatorType, previousScreenId, true);
    if (matchedDuplicatedElements.size() == 1) {
      this.resetOnUpdateIfEligible(matchedDuplicatedElements.get(0));
    }
  }

  private void resetOnUpdateIfEligible(Element element){
    element.setIsDuplicated(false);
    this.save(element);
  }

  public void importXML(BackupDTO importDTO) throws IOException, ResourceNotFoundException {
    if (!importDTO.getIsElementEnabled()) return;
    log.debug("import process for ui-identifier initiated");
    if (importDTO.getIsCloudImport())
    importFiles("ui_identifiers", importDTO);
    else
      importFiles("elements", importDTO);
    log.debug("import process for elements completed");
  }

  @Override
  public List<Element> readEntityListFromXmlData(String xmlData, XmlMapper xmlMapper, BackupDTO importDTO) throws JsonProcessingException {
    if (importDTO.getIsCloudImport()) {
      return elementMapper.mapCloudElementsList(xmlMapper.readValue(xmlData, new TypeReference<List<ElementCloudXMLDTO>>() {
      }));
    }
    else{
      return elementMapper.mapElementsList(xmlMapper.readValue(xmlData, new TypeReference<List<ElementXMLDTO>>() {
      }));
    }
  }

  @Override
  public Optional<Element> findImportedEntity(Element element, BackupDTO importDTO) {
    Optional<Element> previous = elementRepository.findAllByWorkspaceVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), element.getId());
    return previous;
  }

  @Override
  public Element processBeforeSave(Optional<Element> previous, Element present, Element toImport, BackupDTO importDTO) {
    present.setImportedId(present.getId());
    if (previous.isPresent() && importDTO.isHasToReset()) {
      present.setId(previous.get().getId());
    } else {
      present.setId(null);
    }
    Optional<ElementScreenName> uiIdentifierScreenName = screenNameService.getRecentImportedEntity(importDTO, present.getScreenNameId());
    if(uiIdentifierScreenName.isPresent())
      present.setScreenNameId(uiIdentifierScreenName.get().getId());
    present.setWorkspaceVersionId(importDTO.getWorkspaceVersionId());
    return present;
  }


  @Override
  public Element copyTo(Element element) {
    return elementMapper.copy(element);
  }

  @Override
  public Element save(Element element) {
    return elementRepository.save(element);
  }


  @Override
  public Optional<Element> getRecentImportedEntity(BackupDTO importDTO, Long... ids) {
    Long importedId = ids[0];
    return elementRepository.findAllByWorkspaceVersionIdAndImportedId(importDTO.getWorkspaceVersionId(), importedId);
  }

  public Optional<Element> findImportedEntityHavingSameName(Optional<Element> previous, Element current, BackupDTO importDTO) {
    return elementRepository.findByNameAndWorkspaceVersionId(current.getName(), importDTO.getWorkspaceVersionId());
  }

  public boolean hasImportedId(Optional<Element> previous) {
    return previous.isPresent() && previous.get().getImportedId() != null;
  }

  public boolean isEntityAlreadyImported(Optional<Element> previous, Element current) {
    return previous.isPresent() && previous.get().getImportedId() != null && previous.get().getImportedId().equals(current.getId());
  }

  @Override
  public boolean hasToSkip(Element element, BackupDTO importDTO) {
    return false;
  }

  @Override
  void updateImportedId(Element element, Element previous, BackupDTO importDTO) {
    previous.setImportedId(element.getId());
    save(previous);
  }

}
