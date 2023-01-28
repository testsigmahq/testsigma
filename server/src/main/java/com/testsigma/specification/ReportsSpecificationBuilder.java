/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.AddonNaturalTextAction;
import com.testsigma.model.Report;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ReportsSpecificationBuilder extends BaseSpecificationsBuilder {

    private Specification<Report> result;

    public ReportsSpecificationBuilder() {
        super(new ArrayList<>());
    }

    public Specification<Report> build() {
        if (params.size() == 0) {
            return null;
        }

        result = new ReportsSpecification(params.get(0));
        params.forEach((searchCriteria) -> result =
                Specification.where(result).and(new ReportsSpecification(searchCriteria)));
        return result;
    }
}

