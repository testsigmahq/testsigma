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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Setter
@XmlRootElement(name = "testsuite")
public class JUNITTestSuiteNodeDTO {
  @XmlAttribute
  private String name;

  @XmlAttribute
  private String timestamp;

  @XmlAttribute
  private Integer tests;

  @XmlAttribute
  private Integer failures;

  @XmlAttribute
  private Integer errors;
  @XmlAttribute
  private String time;

  @XmlElementWrapper(name = "properties")
  @XmlElement(name = "property")
  private List<JUNITPropertyDTO> properties;

  @XmlElement(name = "testcase")
  private List<JUNITTestCaseNodeDTO> JUNITTestCaseNodeDTOS;

  @XmlElement(name = "system-out")
  private String systemOut;

  @XmlElement(name = "system-err")
  private String systemErr;
}
