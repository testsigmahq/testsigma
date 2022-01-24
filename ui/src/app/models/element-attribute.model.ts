/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../shared/models/base.model";
import {alias, deserialize, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";

export class ElementAttribute extends Base implements Deserializable {
  @serializable(alias('attribute'))
  public name: string;
  @serializable
  public operator: string;
  @serializable
  public selected: boolean
  @serializable
  public value: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ElementAttribute, input));
  }
}
