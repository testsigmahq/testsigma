/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {deserialize, serializable} from 'serializr';

export class Attachment extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public preSignedURL: URL;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Attachment, input));
  }
}
