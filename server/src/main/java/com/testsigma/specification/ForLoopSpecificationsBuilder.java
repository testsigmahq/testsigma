package com.testsigma.specification;

import com.testsigma.model.ForLoopCondition;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ForLoopSpecificationsBuilder extends BaseSpecificationsBuilder {

    public ForLoopSpecificationsBuilder() {
        super(new ArrayList<>());
    }

    public Specification<ForLoopCondition> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new ForLoopSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new ForLoopSpecification(params.get(i)));
        }

        return result;
    }
}