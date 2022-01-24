import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {DataSourceService} from "../shared/services/data-source.service";
import {TestSuite} from "../models/test-suite.model";

@Injectable({
  providedIn: 'root'
})
export class TestSuiteService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestSuite>> {
    return this.http.get<Page<TestSuite>>(this.URLConstants.testSuitesUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestSuite>().deserialize(data, TestSuite)),
      catchError(() => throwError('Problem while fetching TestSuites'))
    )
  }

  public show(id: Number): Observable<TestSuite> {
    return this.http.get<TestSuite>(this.URLConstants.testSuitesUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestSuite().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestSuite'))
    )
  }

  public update(testSuite: TestSuite): Observable<TestSuite> {
    return this.http.put<TestSuite>(this.URLConstants.testSuitesUrl + "/" + testSuite.id, testSuite.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestSuite().deserialize(data)),
      catchError((exception) => throwError(exception))
    )
  }

  public create(testSuite: TestSuite): Observable<TestSuite> {
    return this.http.post<TestSuite>(this.URLConstants.testSuitesUrl, testSuite.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestSuite().deserialize(data)),
      catchError((exception) => throwError(exception))
    )
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testSuitesUrl + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting TestSuite::' + id))
    );
  }

  public bulkDestroy(ids: number[]): Observable<any> {
    return this.http.delete<any>(this.URLConstants.testSuitesUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: new HttpParams().set("ids[]", ids.toString())
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting TestSuites'))
    );
  }
}
