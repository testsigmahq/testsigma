import {Injectable} from '@angular/core';
import {Observable} from "rxjs/internal/Observable";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {HttpClient, HttpParams} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {TestDataSet} from "../models/test-data-set.model";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";

@Injectable({
  providedIn: 'root'
})
export class TestDataSetService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<TestDataSet>> {
    return this.http.get<Page<TestDataSet>>(this.URLConstants.dataSetsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestDataSet>().deserialize(data, TestDataSet)),
      catchError(() => throwError('Problem while fetching test data profile'))
    )
  }

  public bulkDestroy(ids: number[]): Observable<void> {
    let params = new HttpParams().set("ids[]", ids.toString());
    return this.http.delete<void>(this.URLConstants.dataSetsUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((exception) => throwError(exception))
    );
  }
}
