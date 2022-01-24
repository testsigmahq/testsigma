/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Log4j2
@Data
@Entity
@DiscriminatorValue("TEST_PLAN")
public class TestPlan extends AbstractTestPlan {

}
