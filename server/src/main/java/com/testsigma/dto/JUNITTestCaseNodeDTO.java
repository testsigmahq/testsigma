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
import javax.xml.bind.annotation.XmlRootElement;

@Setter
@XmlRootElement(name = "testcase")
public class JUNITTestCaseNodeDTO {
  @XmlAttribute
  private String name;

  @XmlAttribute(name = "classname")
  private String className;

  @XmlAttribute
  private String time;

  @XmlElement
  private String failure;

  public Boolean hasFailure() {
    return (failure != null);
  }
}
