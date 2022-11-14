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
        /*for(SearchCriteria param : params) {
            if(param.getKey().equals("integration")) {
                param.setKey("application.workspace");
            }
        }*/

        Specification<EntityExternalMapping> result = new EntityExternalMappingsSpecification(params.get(0));
        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new EntityExternalMappingsSpecification(params.get(i)));
        }
        return result;
    }
}
