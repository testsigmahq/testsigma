/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, object, optional, serializable, SKIP} from 'serializr';
import {TestSuiteResult} from "./test-suite-result.model";

export class TestSuite extends Base implements PageObject {
  @serializable
  public id: number;

  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public workspaceVersionId: number;
  @serializable
  public preRequisite: number;
  @serializable(optional(object(TestSuite)))
  public preRequisiteSuite: TestSuite;
  @serializable(custom((v) => SKIP, v => {
    if (v)
      return new TestSuiteResult().deserialize(v);
  }))
  public lastRun: TestSuiteResult;
  @serializable(custom(v => v, v => SKIP))
  public testCaseIds: number[];
  @serializable(custom(v => v, v => SKIP))
  public tags: String[];

  public isSelected: Boolean;
  public selected: Boolean;

  public parentSuite: TestSuite;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestSuite, input));
  }
}
