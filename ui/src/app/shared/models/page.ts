/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {PageObject} from "app/shared/models/page-object";
import {Pageable} from "app/shared/models/pageable";
import {Type} from "@angular/core";

export class Page<T extends PageObject> {
  public content: T[];
  public totalElements: number;
  public totalPages: number;
  public pageable: Pageable;
  public numberOfElements: number;
  public first: boolean;
  public last: boolean;
  public empty: boolean;

  deserialize(input: any, classRef: Type<PageObject>): this {
    Object.assign(this, input);
    this.content = input.content.map(data => new classRef().deserialize(data));
    this.pageable = new Pageable().deserialize(input.pageable);
    return this;
  }
}
