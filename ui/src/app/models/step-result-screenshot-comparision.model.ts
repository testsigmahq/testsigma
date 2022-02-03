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
import {custom, deserialize, object, optional, serializable, SKIP} from 'serializr';
import {TestStepResult} from "./test-step-result.model";
import {TestStepScreenshot} from "./test-step-screenshot.model";
import {Shape} from "./shape.model";

export class StepResultScreenshotComparision extends Base implements PageObject {
  @serializable
  public testStepId: Number;
  @serializable
  public testStepResultId: Number;
  @serializable
  public testStepBaseScreenshotId: Number;
  @serializable
  public similarityScore: Number;
  @serializable(custom(
    v => {
      let returnValue = [];
      v.forEach((coOrdinate) => {
        returnValue.push(coOrdinate.serialize())
      })
      return JSON.stringify(returnValue);
    },
    v => {
      let returnValue: Shape[] = [];
      JSON.parse(v || "[]").forEach(coOrdinate => {
        returnValue.push(new Shape().deserialize(coOrdinate));
      })
      return returnValue;
    }
  ))
  public diffCoordinates: Shape[];
  @serializable(custom(
    v => {
      let returnValue = [];
      v.forEach((coOrdinate) => {
        returnValue.push(coOrdinate.serialize())
      })
      return JSON.stringify(returnValue);
    },
    v => {
      let returnValue: Shape[] = [];
      JSON.parse(v || "[]").forEach(coOrdinate => {
        returnValue.push(new Shape().deserialize(coOrdinate));
      })
      return returnValue;
    }
  ))
  public ignoreCoordinates: Shape[];
  @serializable(custom(v => SKIP, v => JSON.parse(v)))
  public imageShape: Number[];
  @serializable
  public screenShotURL: URL;
  @serializable(optional(object(TestStepResult)))
  public testStepResult: TestStepResult;
  @serializable(object(TestStepScreenshot))
  public testStepScreenshot: TestStepScreenshot;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(StepResultScreenshotComparision, input));
  }
}
