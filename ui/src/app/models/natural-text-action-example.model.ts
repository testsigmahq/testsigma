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
import {NaturalTextActions} from "./natural-text-actions.model";

export class NaturaltextActionExample extends Base implements PageObject {

  @serializable
  public naturalTextActionId: number;
  @serializable
  public description: String;
  @serializable
  public example: String;
  @serializable
  public workspace: String;
  @serializable
  public data: String;

  public template: NaturalTextActions;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(NaturaltextActionExample, input));
  }

}
