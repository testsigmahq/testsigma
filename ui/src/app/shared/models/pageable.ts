/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Deserializable} from "app/shared/models/deserializable";
import {deserialize, object, serializable} from "serializr";
import {Sort} from "app/shared/models/sort";
import {Base} from "app/shared/models/base.model";

export class Pageable extends Base implements Deserializable {
  @serializable(object(Sort))
  public sort: Sort;
  @serializable
  public offset: number;
  @serializable
  public pageNumber: number;
  @serializable
  public pageSize: number;
  @serializable
  public paged: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Pageable, input));
  }
}
