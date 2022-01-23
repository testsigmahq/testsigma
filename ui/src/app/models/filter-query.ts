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
import {custom, deserialize, serializable} from "serializr";
import {FilterOperation} from "../enums/filter.operation.enum";
import {TestCaseStatus} from "../enums/test-case-status.enum";
import {ResultConstant} from "../enums/result-constant.enum";

export class FilterQuery extends Base implements Deserializable {
  @serializable
  public key: string;
  @serializable
  public operation: FilterOperation;
  @serializable(custom(v => v, v => v))
  public value: boolean | number | string | number[] | string[] | TestCaseStatus[] | ResultConstant[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(FilterQuery, input));
  }

}
