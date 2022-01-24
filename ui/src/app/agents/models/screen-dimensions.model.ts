/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

import {Deserializable} from "../../shared/models/deserializable";
import {deserialize, primitive, serializable} from "serializr";

export class ScreenDimensions implements Deserializable {
  @serializable(primitive())
  screenWidth: number;
  @serializable(primitive())
  screenHeight: number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ScreenDimensions, input));
  }

}
