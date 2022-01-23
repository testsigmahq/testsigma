/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

export enum FilterOperation {
  EQUALITY = "EQUALITY",
  NEGATION = "NEGATION",
  GREATER_THAN = "GREATER_THAN",
  LESS_THAN = "LESS_THAN",
  LIKE = "LIKE",
  STARTS_WITH = "STARTS_WITH",
  ENDS_WITH = "ENDS_WITH",
  CONTAINS = "CONTAINS",
  IN = "IN",
  NOT_IN = "NOT_IN"
}
