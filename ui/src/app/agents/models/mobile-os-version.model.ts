/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../../shared/models/base.model";
import {deserialize, list, object, serializable} from 'serializr';
import {Deserializable} from "../../shared/models/deserializable";
import {CloudDevice} from "./cloud-device.model";

export class MobileOsVersion extends Base implements Deserializable {
  @serializable
  public osName: String;
  @serializable
  public osDisplayName: String;
  @serializable(list(object(CloudDevice)))
  public platformDevices: CloudDevice[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(MobileOsVersion, input));
  }

  // @ts-ignore
  get name(): String {
    return this.osDisplayName;
  }

  // @ts-ignore
  get id(): any {
    return this.osName;
  }
}
