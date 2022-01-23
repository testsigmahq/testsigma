/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Deserializable} from "app/shared/models/deserializable";
import {deserialize, list, object, primitive, serializable} from "serializr";
import {AuthUser} from "../../models/auth-user.model";

export class Session implements Deserializable {
  @serializable
  public id: String;
  @serializable(object(AuthUser))
  public user: AuthUser;
  @serializable
  public serverUrl: string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Session, input));
  }
}
