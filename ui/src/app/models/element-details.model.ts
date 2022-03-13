/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {deserialize, serializable} from 'serializr';
import {ElementLocatorType} from "../enums/element-locator-type.enum";

export class ElementDetails extends Base implements Deserializable {
  @serializable
  public elementName: String;
  @serializable
  public element: String;
  @serializable
  private findByType: ElementLocatorType;

  public isElementChanged: Boolean;

  public showInfo = false;
  deserialize(input: any): this {
    return Object.assign(this, deserialize(ElementDetails, input));
  }
}
