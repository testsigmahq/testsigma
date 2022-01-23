/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {custom, deserialize, serializable, SKIP} from 'serializr';
import {Serializable} from "../shared/models/serializable";
import {PageObject} from "../shared/models/page-object";

export class TestDataSet extends Base implements Deserializable, Serializable{
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public expectedToFail: Boolean;
  @serializable(custom(v => {
    let dataStrings ="";
    for(let key in v)  {
      dataStrings += ",\"" + key.trim() + "\"" + ":" + "\"" + v[key] +"\"";
    }
    return "{" + dataStrings.substring(1) + "}";
  }, v => v))
  public data:JSON;
  public selected: Boolean = false;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestDataSet, input));
  }

}
