/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum OnAbortedAction {
  Reuse_Session, Restart_Session
}
