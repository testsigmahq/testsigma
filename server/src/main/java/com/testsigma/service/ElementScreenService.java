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
import com.testsigma.dto.export.ElementScreenNameXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementScreenNameMapper;
import com.testsigma.model.ElementScreenName;
import com.testsigma.repository.ElementScreenNameRepository;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import com.testsigma.web.request.ElementScreenNameRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class ElementScreenService extends XMLExportService<ElementScreenName> {
  private final ElementScreenNameRepository elementScreenNameRepository;
  private final ElementScreenNameMapper elementScreenNameMapper;

  public Page<ElementScreenName> findAll(Specification<ElementScreenName> specification, Pageable page) {
    return elementScreenNameRepository.findAll(specification, page);
  }

  public ElementScreenName save(ElementScreenNameRequest screenNameKey) {
    Optional<ElementScreenName> result =
      elementScreenNameRepository.findByNameAndWorkspaceVersionId(screenNameKey.getName(), screenNameKey.getWorkspaceVersionId());
    ElementScreenName screenName = null;
    if (!result.isPresent()) {
      screenName = elementScreenNameMapper.map(screenNameKey);
      screenName.setName(screenNameKey.getName());
      screenName.setWorkspaceVersionId(screenNameKey.getWorkspaceVersionId());
      screenName = elementScreenNameRepository.save(screenName);
    } else {
      screenName = result.get();
      screenName = elementScreenNameRepository.save(screenName);
    }
    return screenName;
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsElementScreenNameEnabled()) return;
    log.debug("backup process for element screen name  initiated");
    writeXML("element_screen_names", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for element screen name  completed");
  }

  public Specification<ElementScreenName> getExportXmlSpecification(BackupDTO backupDTO) throws ResourceNotFoundException {
    SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    ElementScreenNameSpecificationsBuilder elementScreenNameSpecificationsBuilder = new ElementScreenNameSpecificationsBuilder();
    elementScreenNameSpecificationsBuilder.params = params;
    return elementScreenNameSpecificationsBuilder.build();
  }

  @Override
  protected List<ElementScreenNameXMLDTO> mapToXMLDTOList(List<ElementScreenName> list) {
    return elementScreenNameMapper.mapElementScreenNameList(list);
  }
}
