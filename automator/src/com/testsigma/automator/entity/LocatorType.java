/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.automator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LocatorType {
  xpath,
  csspath,
  id_value,
  name,
  link_text,
  partial_link_text,
  class_name,
  tag_name,
  accessibility_id

}
