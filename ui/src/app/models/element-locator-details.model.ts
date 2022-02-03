/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../shared/models/base.model";
import {alias, deserialize, list, object, optional, serializable} from 'serializr';
import {Deserializable} from "../shared/models/deserializable";
import {ElementAttribute} from "./element-attribute.model";

export class ElementElementDetails extends Base implements Deserializable {
  @serializable(alias('data', optional(list(object(ElementAttribute)))))
  public attributes: ElementAttribute[];
  @serializable
  public path: string;
  @serializable
  public tag: String
  @serializable
  public type: string;
  public viewMore: Boolean;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(ElementElementDetails, input));
  }
}
