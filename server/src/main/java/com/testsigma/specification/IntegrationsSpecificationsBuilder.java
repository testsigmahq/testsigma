package com.testsigma.specification;

import com.testsigma.model.Integrations;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;

public class IntegrationsSpecificationsBuilder extends BaseSpecificationsBuilder {

    public IntegrationsSpecificationsBuilder() {
        super(new ArrayList<>());
    }

    public Specification<Integrations> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new IntegrationsSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new IntegrationsSpecification(params.get(i)));
        }

        return result;
    }
}
