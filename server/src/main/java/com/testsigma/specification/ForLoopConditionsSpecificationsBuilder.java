
package com.testsigma.specification;

import com.testsigma.model.ForLoopCondition;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ForLoopConditionsSpecificationsBuilder extends BaseSpecificationsBuilder {

    public ForLoopConditionsSpecificationsBuilder() {
        super(new ArrayList<>());
    }

    public Specification<ForLoopCondition> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new ForLoopConditionsSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new ForLoopConditionsSpecification(params.get(i)));
        }

        return result;
    }
}