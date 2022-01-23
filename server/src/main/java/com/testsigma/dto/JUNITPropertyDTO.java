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
import javax.xml.bind.annotation.XmlRootElement;

@Setter
@XmlRootElement(name = "property")
public class JUNITPropertyDTO {
  @XmlAttribute
  private String name;

  @XmlAttribute
  private String value;
}
