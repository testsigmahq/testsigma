/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, serializable} from 'serializr';
import {StepDetailsTestDataFunction} from "./step-details-test-data-function.model";

export class TestDataDetails extends Base implements Deserializable {
  @serializable
  public testDataName: String;
  @serializable
  public testDataValue: String;
  @serializable
  public testDataType: String;
  @serializable(custom(v => v, v => v))
  public testDataFunction: Map<String, Object> ;
  @serializable(custom(v => v, v => {
    if(v) {
      return new StepDetailsTestDataFunction().deserialize(v);
    }
  }))
  public testDataFunctionEntity: StepDetailsTestDataFunction;

  public isOpen = false;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestDataDetails, input));
  }
}
