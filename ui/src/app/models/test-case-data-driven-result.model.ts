/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {custom, deserialize, object, serializable, SKIP} from 'serializr';
import {TestCaseResult} from "./test-case-result.model";
import {TestDataSet} from "./test-data-set.model";

export class TestCaseDataDrivenResult extends Base implements PageObject {
  @serializable
  public testCaseId: number;
  @serializable
  public testDataName: String;
  @serializable(custom(v => SKIP, v => new TestDataSet().deserialize(JSON.parse(v))))
  public testData: TestDataSet;
  @serializable
  public envRunId: Number;
  @serializable
  public testCaseResultId: Number;
  @serializable
  public iterationResultId: Number;
  @serializable(object(TestCaseResult))
  public iterationResult: TestCaseResult;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCaseDataDrivenResult, input));
  }
}
