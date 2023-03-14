/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, deserialize, object, optional, serializable} from 'serializr';
import {ForLoopData} from "./for-loop-data.model";

export class TestStepResultMetaForLoop extends Base implements Deserializable {
  @serializable
  public iteration: String;
  @serializable(alias('testdata'))
  public testDataName: String;
  @serializable
  public index: Number;
  @serializable( optional(object(ForLoopData)))
  public forLoopCondition: ForLoopData

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepResultMetaForLoop, input));
  }
}
