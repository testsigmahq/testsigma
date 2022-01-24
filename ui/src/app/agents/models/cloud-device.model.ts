/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../../shared/models/base.model";
import {Deserializable} from "../../shared/models/deserializable";
import {deserialize, serializable} from 'serializr';

export class CloudDevice extends Base implements Deserializable {
  @serializable
  public displayName: String;
  @serializable
  public isAvailable: Boolean;
  @serializable
  public name: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(CloudDevice, input));
  }

  // @ts-ignore
  get disabled(): Boolean {
    return !this.isAvailable;
  }

  get deviceName() {
    return this.name;
  }
}
