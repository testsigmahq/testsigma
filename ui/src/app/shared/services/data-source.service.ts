/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Pageable} from "../models/pageable";
import {Page} from "../models/page";
import {PageObject} from "../models/page-object";
import {Observable} from "rxjs";

export interface DataSourceService {
  findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<PageObject>>;
}
