package com.testsigma.controller.recorder;

/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

import com.testsigma.dto.ElementDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.mapper.recorder.UiIdentifierMapper;
import com.testsigma.model.*;
import com.testsigma.model.recorder.UiIdentifierDTO;
import com.testsigma.model.recorder.UiIdentifierRequest;
import com.testsigma.service.*;
import com.testsigma.specification.ElementSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.web.request.ElementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "/os_recorder/ui_identifiers")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UiIdentifiersController {
    private final ElementService uiIdentifierService;
    private final ElementMapper elementMapper;
    private final UiIdentifierMapper uiIdentifierMapper;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasPermission('FIELD_DEFINITION','WRITE')")
    public UiIdentifierDTO create(@RequestBody @Valid UiIdentifierRequest uiIdentifierRequest) throws SQLException, ResourceNotFoundException {
        ElementRequest uiIdentifier = uiIdentifierMapper.mapRequest(uiIdentifierRequest);
        Element element = elementMapper.map(uiIdentifier);
        if(element.getCreatedType() == ElementCreateType.CHROME){
            element = uiIdentifierService.createUiIdentifierFromRecorder(element);
            return uiIdentifierMapper.mapDTO(elementMapper.map(element));
        }else{
            element = uiIdentifierService.create(element);
            return uiIdentifierMapper.mapDTO(elementMapper.map(element));
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission('FIELD_DEFINITION','READ')")
    public Page<UiIdentifierDTO> index(ElementSpecificationsBuilder builder, Pageable pageable, @RequestParam(required = false) String uiIdentifierName) {

        Long applicationVersion = null;
        String name = "";
        String screenName = "";
        String previousStepElementName = null;
        for (SearchCriteria param : builder.params) {
            if (param.getKey().equals("applicationVersionId") || param.getKey().equals("workspaceVersionId")) {
                applicationVersion = Long.parseLong(param.getValue().toString());
            } else if (param.getKey().equals("name")) {
                name = param.getValue().toString();
            } else if (param.getKey().equals("screenName")) {
                screenName = param.getValue().toString();
            } else if (param.getKey().equals("previousStepElementName")) {
                previousStepElementName = param.getValue().toString();
            }
        }

        Page<Element> elements;
        if(previousStepElementName != null) {
            elements = uiIdentifierService.findAllSortedByPreviousStepElement(pageable, applicationVersion, name, screenName, previousStepElementName);
        } else {
            Specification<Element> spec = builder.build();
            elements = uiIdentifierService.findAll(spec, pageable);
        }
        List<UiIdentifierDTO> uiIdentifierDTOS = uiIdentifierMapper.mapDTO(elementMapper.map(elements.getContent()));
        return new PageImpl<>(uiIdentifierDTOS, pageable, elements.getTotalElements());
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasPermission('FIELD_DEFINITION','READ')")
    public UiIdentifierDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
        Element element = uiIdentifierService.find(id);
        ElementDTO elementDTO = elementMapper.map(element);
        return uiIdentifierMapper.mapDTO(elementDTO);
    }


    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasPermission('FIELD_DEFINITION','WRITE')")
    public UiIdentifierDTO update(@PathVariable("id") Long id,
                                  @RequestBody UiIdentifierRequest uiIdentifierRequest,
                                  @RequestParam(value = "reviewSubmittedFrom", required = false) String reviewSubmittedFrom)
            throws ResourceNotFoundException, SQLException {
        Element element = uiIdentifierService.find(id);
        ElementRequest elementRequest = uiIdentifierMapper.mapRequest(uiIdentifierRequest);
        String oldName = element.getName();
        String previousLocatorValue = element.getLocatorValue();
        Long previousScreenNameId = element.getScreenNameId();
        LocatorType previousLocatorType = element.getLocatorType();
        elementMapper.merge(elementRequest, element);
        uiIdentifierService.update(element, oldName, previousLocatorValue, previousLocatorType, previousScreenNameId);
        return uiIdentifierMapper.mapDTO(elementMapper.map(element));
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasPermission('FIELD_DEFINITION','FULL_ACCESS')")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) throws ResourceNotFoundException {
        uiIdentifierService.delete(uiIdentifierService.find(id));
    }

}


