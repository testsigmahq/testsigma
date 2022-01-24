/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {PageObject} from "../shared/models/page-object";
import {deserialize, object, serializable, SKIP, custom, optional} from 'serializr';
import {StatusConstant} from "../enums/status-constant.enum";
import {TestSuite} from "./test-suite.model";
import {TestDeviceResult} from "./test-device-result.model";
import {ResultBase} from "./result-base.model";
import {TestCase} from "./test-case.model";

export class TestSuiteResult extends ResultBase implements PageObject {

  @serializable
  public id: number;
  @serializable
  public environmentResultId: number;
  @serializable
  public suiteId: number;
  @serializable
  public status: StatusConstant;
  @serializable
  public message: String;
  @serializable
  public position: number;
  @serializable(custom(_v => SKIP, v => {
    return new TestSuite().deserialize(v);
  }))
  public testSuite: TestSuite;
  @serializable(object(TestDeviceResult))
  public testDeviceResult: TestDeviceResult;
  @serializable(custom(_v => SKIP, v => v))
  public sessionId: string;
  @serializable
  public videoURL: URL;
  @serializable(custom(v => SKIP, v => v))
  public logURLS: Map<String, URL>;
  @serializable(optional(object(TestSuiteResult)))
  public childResult: TestSuiteResult;
  @serializable
  public reRunParentId: number;
  @serializable
  public isVisuallyPassed: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestSuiteResult, input));
  }
}
