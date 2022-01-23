/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Deserializable} from "../../shared/models/deserializable";
import {deserialize, serializable} from 'serializr';
import {Base} from "../../shared/models/base.model";

export class Browser extends Base implements Deserializable {
  @serializable
  public name: String;
  @serializable
  public version: String;
  @serializable
  public majorVersion: String;
  @serializable
  public arch: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Browser, input));
  }

  get isChrome() {
    return this.name.toUpperCase() == 'GOOGLECHROME' || this.name.toUpperCase() == 'CHROME';
  }

  get isFirefox() {
    return this.name.toUpperCase() == 'FIREFOX';
  }

  get isSafari() {
    return this.name.toUpperCase() == 'SAFARI';
  }

  get isEdge() {
    return this.name.toUpperCase() == 'EDGE';
  }
}
