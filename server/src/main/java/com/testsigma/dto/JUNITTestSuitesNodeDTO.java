/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "testsuites")
@Setter
public class JUNITTestSuitesNodeDTO {
  @XmlElement(name = "testsuite")
  private List<JUNITTestSuiteNodeDTO> JUNITTestSuiteNodeDTOS;
}
