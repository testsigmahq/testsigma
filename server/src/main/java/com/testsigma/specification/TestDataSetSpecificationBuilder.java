package com.testsigma.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestDataSetSpecificationBuilder extends BaseSpecificationsBuilder {

    public TestDataSetSpecificationBuilder() {
        super(new ArrayList<>());
    }

    @Override
    public Specification build() {
        if(params.size() == 0){
            return null;
        }

        return new TestDataSetSpecification(params.get(0));
    }
}

