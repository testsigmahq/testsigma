/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;
import org.hibernate.annotations.DiscriminatorFormula;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Data
@DiscriminatorFormula("case when entity_type is null then 'TEST_SUITE' else entity_type end")
@DiscriminatorValue("TEST_SUITE")
public class TestSuite extends AbstractTestSuite {
}
