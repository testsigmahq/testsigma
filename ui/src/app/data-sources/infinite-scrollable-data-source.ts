/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Pageable} from "../shared/models/pageable";
import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable, Subscription} from 'rxjs';
import {PageObject} from "../shared/models/page-object";
import {DataSourceService} from "../shared/services/data-source.service";

export class InfiniteScrollableDataSource extends DataSource<PageObject> {
  public cachedItems = Array.from<PageObject>({length: 0});
  protected dataStream = new BehaviorSubject<(PageObject)[]>(this.cachedItems);
  protected subscription = new Subscription();

  protected pageSize: number;
  protected lastPageable: Pageable;
  protected isLastPage: boolean;
  public isFetching: boolean;
  protected query: string;
  protected sortBy: string;
  public isEmpty: Boolean = false;
  public totalElements : number = 0;

  constructor(protected dataSourceService: DataSourceService, query?: string, sortBy?: string, pageSize?: number) {
    super();
    this.query = query;
    this.sortBy = sortBy;
    this.pageSize = pageSize || 10;
    this.lastPageable = new Pageable();
    this.lastPageable.pageNumber = -1;
    this.lastPageable.pageSize = this.pageSize;
    // Start with some data.
    this._fetchFactPage();
  }

  connect(collectionViewer: CollectionViewer): Observable<(PageObject)[] | ReadonlyArray<PageObject>> {
    this.subscription.add(collectionViewer.viewChange.subscribe(range => {
      console.debug(range);
      const currentPage = this._getPageForIndex(range.end);

      if (currentPage && range) {
        console.debug(currentPage, this.lastPageable.pageNumber);
      }

      if (currentPage > this.lastPageable.pageNumber && !this.isLastPage) {
        this._fetchFactPage();
      }
    }));
    return this.dataStream;
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.subscription.unsubscribe();
  }

  protected _fetchFactPage(): void {
    this.isFetching = true;
    this.lastPageable.pageNumber += 1;
    this.dataSourceService.findAll(this.query, this.sortBy, this.lastPageable).subscribe(res => this.processResponse(res));
  }

  protected processResponse(res): void {
    this.cachedItems = this.cachedItems.concat(res.content);
    this.isLastPage = res.last;
    this.dataStream.next(this.cachedItems);
    this.isFetching = false;
    this.isEmpty = res.content.length < 1;
    this.totalElements = res.totalElements;
  }

  private _getPageForIndex(i: number): number {
    return Math.floor(i / this.pageSize);
  }
}
