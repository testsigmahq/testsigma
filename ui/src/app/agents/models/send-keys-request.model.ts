/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

import {Base} from "../../shared/models/base.model";
import {MobileElement} from "./mobile-element.model";
import {object, primitive, serializable} from 'serializr';

export class SendKeysRequest extends Base {
  @serializable(object(MobileElement))
  mobileElement: MobileElement;
  @serializable(primitive())
  keys: String;
}
