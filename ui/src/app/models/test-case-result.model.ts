/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {PageObject} from "../shared/models/page-object";
import {custom, deserialize, object, optional, serializable, SKIP} from 'serializr';
import {TestCaseStatus} from "../enums/test-case-status.enum";
import {TestCase} from "./test-case.model";
import {TestSuite} from "./test-suite.model";
import {TestDeviceResult} from "./test-device-result.model";
import {ResultBase} from "./result-base.model";
import {TestCasePriority} from "./test-case-priority.model";
import {TestCaseType} from "./test-case-type.model";
import {TestData} from "./test-data.model";

export class TestCaseResult extends ResultBase implements PageObject {

  @serializable
  public id: number;
  @serializable
  public testCaseId: number;
  @serializable
  public testPlanResultId: number;
  @serializable
  public environmentResultId: number;
  @serializable
  public suiteId: number;
  @serializable
  public iteration: String;
  @serializable
  public isStepGroup: Boolean;
  @serializable
  public message: String;
  @serializable
  public suiteResultId: number;
  @serializable
  public parentId: number;
  @serializable
  public testDataSetName: String;
  @serializable
  public position: number;
  @serializable
  public testCaseTypeId: number;
  @serializable
  public testCaseStatus: TestCaseStatus;
  @serializable
  public priorityId: number;
  @serializable
  public isDataDriven: Boolean;
  @serializable
  public testDataId: number;
  @serializable
  public assignee: number;
  @serializable(custom((v) => SKIP, v => {
    return new TestCase().deserialize(v);
  }))
  public testCase: TestCase;
  @serializable(object(TestSuite))
  public testSuite: TestSuite;
  @serializable(object(TestDeviceResult))
  public testDeviceResult: TestDeviceResult;
  @serializable(optional(object(TestCaseResult)))
  public parentResult: TestCaseResult;
  @serializable(custom(_v => SKIP, v => v))
  public sessionId: string;
  @serializable
  public videoURL: URL;
  @serializable(custom(v => SKIP, v => v))
  public logURLS: Map<String, URL>;
  @serializable(optional(object(TestCaseResult)))
  public childResult: TestCaseResult
  @serializable
  public reRunParentId: number;
  @serializable
  public isVisuallyPassed: boolean;

  public testCasePriority: TestCasePriority;
  public testCaseType: TestCaseType;
  public testDataProfile: TestData;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCaseResult, input));
  }

  get lastRun(){
    return this.getLastTestCaseResult(this);
  }

  getLastTestCaseResult(testCaseResult: TestCaseResult){
    if(testCaseResult.childResult == null)
      return testCaseResult;
    return this.getLastTestCaseResult(testCaseResult.childResult);
  }

  checkIfChildRunExists(){
    return this.lastRun.id != this.id;
  }
}
