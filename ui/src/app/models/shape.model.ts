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
import {deserialize, serializable} from 'serializr';

export class Shape extends Base implements Deserializable {
  @serializable
  public x: Number;
  @serializable
  public y: Number;
  @serializable
  public w: Number;
  @serializable
  public h: Number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Shape, input));
  }

}
