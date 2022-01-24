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

export class AgentInfo implements Deserializable {
  @serializable(primitive())
  uniqueId: String;
  @serializable(primitive())
  enabled: Boolean;
  @serializable(primitive())
  visibleToAll: Boolean;
  @serializable(primitive())
  agentVersion: String;
  @serializable(primitive())
  hostName: String;
  @serializable(primitive())
  osVersion: String;
  @serializable
  isRegistered: Boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AgentInfo, input));
  }

}
