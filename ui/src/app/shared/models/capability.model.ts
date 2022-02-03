/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Deserializable} from "./deserializable";
import {deserialize, serializable, alias} from 'serializr';
import {Base} from "./base.model";

export class Capability extends Base implements Deserializable {
  @serializable
  public name: String;
  @serializable(alias('dataType'))
  public type: String;
  @serializable
  public value: String | Number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Capability, input));
  }

}
