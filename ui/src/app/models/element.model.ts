/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, object, optional, serializable, serialize, SKIP} from 'serializr';
import {ElementCreateType} from "../enums/element-create-type.enum";
import {ElementLocatorType} from "../enums/element-locator-type.enum";
import {MobileElementRect} from "../agents/models/mobile-element-rect.model";
import {ElementMetaData} from "./element-meta-data.model";
import {ElementElementDetails} from "./element-locator-details.model";
import * as moment from "moment";
import {ElementScreenName} from "./element-screen-name.model";

export class Element extends Base implements PageObject {
  public isSelected: Boolean = false;
  @serializable
  public name: String;
  @serializable
  public locatorValue: string;
  @serializable(alias('attributes', custom(v => {
    if (v) {
      return JSON.stringify(v.serialize())
    }
  }, v => {
    if (v) {
      return new ElementElementDetails().deserialize(JSON.parse(v));
    }
  })))
  public elementDetails: ElementElementDetails;
  @serializable
  public createdType: ElementCreateType;
  @serializable
  public locatorType: ElementLocatorType;

  @serializable(custom(v => v
    , v => new ElementScreenName().deserialize(v)
  ))
  public screenNameObj: ElementScreenName = new ElementScreenName();

  public screenName : String;

  @serializable(alias('metadata', custom(v=>v,v=>v) ))
  public metadata: ElementMetaData;
  @serializable
  public workspaceVersionId: number;
  @serializable
  public screenNameId: number;
  @serializable
  public isDynamic: Boolean;
  public mobileElementRect: MobileElementRect;
  public saving: Boolean = false;
  public errors: String[];
  public saved: Boolean = false;

  @serializable(custom(() => SKIP, (v) => {
    if (v)
      return moment(v);
  }))  public createdDate: Date;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Element, input));
  }

  get locatorValueWithSpecialCharacters() {
    return this.locatorValue?.replace(/\*/, 'ts_asterisk').replace(/,/, 'ts_comma');
  }
}
