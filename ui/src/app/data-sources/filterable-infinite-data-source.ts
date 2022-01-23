/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {InfiniteScrollableDataSource} from "./infinite-scrollable-data-source";
import {Pageable} from "../shared/models/pageable";
import {FilterableDataSourceService} from "../shared/services/filterable-data-source.service";

export class FilterableInfiniteDataSource extends InfiniteScrollableDataSource {

  protected readonly filterId: number;
  protected readonly versionId: number;

  constructor(protected dataSourceService: FilterableDataSourceService, query?: string, sortBy?: string, pageSize?: number, filterId?: number, versionId?: number) {
    super(dataSourceService, query, sortBy, pageSize);
    this.query = query;
    this.sortBy = sortBy;
    this.pageSize = pageSize || 10;
    this.lastPageable = new Pageable();
    this.lastPageable.pageNumber = -1;
    this.lastPageable.pageSize = this.pageSize;
    this.filterId = filterId;
    this.versionId = versionId;
    // Start with some data.
    this._fetchFactPage();
  }

  protected _fetchFactPage(): void {
    this.isFetching = true;
    this.lastPageable.pageNumber += 1;
    if (this.query && this.filterId) {
      this.dataSourceService.findAll(this.query, this.sortBy, this.lastPageable).subscribe(res => this.processResponse(res));
    } else if (this.filterId) {
      this.dataSourceService.filter(this.filterId, this.versionId, this.lastPageable, this.sortBy).subscribe(res => this.processResponse(res))
    }
  }
}
