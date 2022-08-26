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

export class SessionResponse implements Deserializable {
  @serializable(primitive())
  sessionId: String;
  status: String;
  hostname: String;
  @serializable(primitive())
  message: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(SessionResponse, input));
  }
}
