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
import {custom, deserialize, serializable} from 'serializr';
import {Shape} from "./shape.model";

export class TestStepScreenshot extends Base implements PageObject {
  @serializable
  public testStepId: Number;
  @serializable
  public testStepResultId: Number;
  @serializable
  public testCaseResultId: Number;
  @serializable
  public environmentResultId: Number;
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
  public ignoredCoordinates: Shape[];
  @serializable
  public screenShotURL: URL;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepScreenshot, input));
  }

}
