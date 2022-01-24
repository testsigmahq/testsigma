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
import {alias, custom, deserialize, object, optional, serializable, SKIP} from 'serializr';
import {TestDataType} from "../enums/test-data-type.enum";
import {Element} from "./element.model";
import {TestStepResultMetaForLoop} from "./test-step-result-meta-for-loop.model";
import {RestResultMetaData} from "./rest-result-meta-data";
import {StepDetails} from "./step-details.model";

export class TestStepResultMetadata extends Base implements Deserializable {

  @serializable
  public action: String;
  @serializable
  public hasPassword: String;
  @serializable
  public testDataType: TestDataType;
  @serializable
  public testDataValue: String;
  @serializable(optional(object(Element)))
  public element: Element;
  @serializable
  public attribute: String;
  @serializable(custom(v => v, v => v))
  public additionalData: JSON;
  @serializable(custom(v => v, v => v))
  public reqEntity: Map<string, any>;
  @serializable
  public preRequisite: Number;
  @serializable(object(RestResultMetaData))
  public restResult: RestResultMetaData;
  @serializable(alias('for_loop', optional(object(TestStepResultMetaForLoop))))
  public forLoop: TestStepResultMetaForLoop;
  @serializable(custom(v => SKIP, v => {
    if (typeof v == 'string') {
      v = JSON.parse(v)
    }
    return new StepDetails().deserialize(v);
  }))
  public stepDetails: StepDetails;
  @serializable(custom(v => v, v => v))
  public runtimeData: JSON;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepResultMetadata, input));
  }

}
