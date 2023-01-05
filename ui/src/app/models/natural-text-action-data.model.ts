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
import {custom, deserialize, serializable} from 'serializr';

export class NaturalTextActionData extends Base implements Deserializable {

  @serializable(custom(v=>v, v=>v))
  public testData: Map<string, string>;
  @serializable
  public element: String;
  @serializable
  public attribute: String;
  @serializable
  public fromElement: String;
  @serializable
  public toElement: String;
  @serializable(custom(v=>v, v=>v))
  public testDataForLoop: Map<string, string>;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(NaturalTextActionData, input));
  }

}
