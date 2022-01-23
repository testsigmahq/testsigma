import {Injectable} from '@angular/core';
import {Pageable} from "../shared/models/pageable";
import {Observable} from "rxjs/internal/Observable";
import {Page} from "../shared/models/page";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {HttpClient, HttpParams} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {TestData} from "../models/test-data.model";

@Injectable({
  providedIn: 'root'
})
export class TestDataService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<TestData>> {
    return this.http.get<Page<TestData>>(this.URLConstants.dataProfileUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestData>().deserialize(data, TestData)),
      catchError(() => throwError('Problem while fetching test data profile'))
    )
  }

  public show(id: Number): Observable<TestData> {
    return this.http.get<TestData>(this.URLConstants.dataProfileUrl + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => {
        return new TestData().deserialize(data)
      }),
      catchError(() => throwError('Problem while fetching test data profile'))
    )
  }

  public delete(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.dataProfileUrl + '/' + id,
      {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError((exception) => throwError(exception))
    )
  }

  public bulkDestroy(ids: number[]): Observable<void> {
    let params = new HttpParams().set("ids[]", ids.toString());
    return this.http.delete<void>(this.URLConstants.dataProfileUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((exception) => throwError(exception))
    );
  }

  public create(testData: TestData): Observable<TestData> {
    return this.http.post(this.URLConstants.dataProfileUrl, testData.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestData().deserialize(data)),
      catchError((exception) => throwError(exception))
    );
  }


  public update(id: number, testData: TestData): Observable<TestData> {
    return this.http.put<TestData>(this.URLConstants.dataProfileUrl + "/" + id, testData.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestData().deserialize(data)),
      catchError(() => throwError('Problem while updating Test Data Profile file'))
    );
  }

}
