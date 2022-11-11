package com.testsigma.specification;

import com.testsigma.model.EntityExternalMapping;
import com.testsigma.specification.BaseSpecificationsBuilder;
import com.testsigma.specification.EntityExternalMappingsSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class EntityExternalMappingsBuilder extends BaseSpecificationsBuilder {

    public EntityExternalMappingsBuilder() {super(new ArrayList<>());}
    @Override
    public Specification<EntityExternalMapping> build() {
        if(params.size() == 0){
            return null;
        }

        Specification<EntityExternalMapping> result = new EntityExternalMappingsSpecification(params.get(0));
        return result;
    }
}
