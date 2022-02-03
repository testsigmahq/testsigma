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
import {DataSourceService} from "./data-source.service";

export interface FilterableDataSourceService extends DataSourceService {
  filter(filterId: number, versionId: number, pageable?: Pageable, sortBy?: string): Observable<Page<PageObject>>;
}
