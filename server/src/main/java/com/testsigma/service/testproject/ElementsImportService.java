package com.testsigma.service.testproject;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ElementMapper;
import com.testsigma.model.*;
import com.testsigma.service.ElementScreenService;
import com.testsigma.service.ElementService;
import com.testsigma.service.EntityExternalMappingService;
import com.testsigma.web.request.testproject.TestProjectElementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class ElementsImportService extends BaseImportService<TestProjectElementRequest> {

    private final EntityExternalMappingService entityExternalMappingService;
    private final ElementService elementService;
    private final ElementScreenService elementScreenService;

    Element checkAndCreateElement(TestProjectElementRequest elementRequest, Long applicationVersionId, Integrations integration) throws ResourceNotFoundException {
        List<EntityExternalMapping> entityExternalMapping = entityExternalMappingService.findByExternalIdAndEntityTypeAndApplicationId(elementRequest.getName(), EntityType.ELEMENT, integration.getId());
        Element element;
        if(!entityExternalMapping.isEmpty()) {
            element = elementService.find(entityExternalMapping.get(0).getEntityId());
        }
        else {
            element = new Element();
            element.setName(elementRequest.getName());
            element.setWorkspaceVersionId(applicationVersionId);
            element.setLocatorType(elementRequest.getLocators().get(0).getLocatorType());
            element.setLocatorValue(elementRequest.getLocators().get(0).getValue());
            element.setScreenNameId(findOrCreateScreenName(applicationVersionId).getId());
            element = elementService.create(element);
            createEntityExternalMappingIfNotExists(element.getName(), EntityType.ELEMENT, element.getId(), integration);
        }
        return element;
    }

    private ElementScreenName findOrCreateScreenName(Long workspaceVersionId){
        List<ElementScreenName> screenNames = elementScreenService.findAllByWorkspaceVersionId(workspaceVersionId);
        if(screenNames.isEmpty()){
            ElementScreenName screenName = new ElementScreenName();
            screenName.setName(elementScreenService.DEFAULT_SCREEN_NAME);
            screenName.setWorkspaceVersionId(workspaceVersionId);
            return elementScreenService.save(screenName);
        }
        return screenNames.get(0);
    }
}
