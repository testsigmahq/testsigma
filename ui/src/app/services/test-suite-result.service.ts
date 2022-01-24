import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestSuiteResult} from "../models/test-suite-result.model";
import {DataSourceService} from "../shared/services/data-source.service";

@Injectable({
  providedIn: 'root'
})
export class TestSuiteResultService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestSuiteResult>> {
    return this.http.get<Page<TestSuiteResult>>(this.URLConstants.testSuiteResultsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestSuiteResult>().deserialize(data, TestSuiteResult)),
      catchError(() => throwError('Problem while fetching TestSuiteResults'))
    )
  }

  public show(id: Number): Observable<TestSuiteResult> {
    return this.http.get<TestSuiteResult>(this.URLConstants.testSuiteResultsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestSuiteResult().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestSuiteResult'))
    )
  }

  public update(testSuiteResult: TestSuiteResult): Observable<TestSuiteResult> {
    return this.http.put<TestSuiteResult>(this.URLConstants.testSuiteResultsUrl + "/" + testSuiteResult.id, testSuiteResult.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestSuiteResult().deserialize(data)),
      catchError(() => throwError('Problem while update TestSuiteResult'))
    )
  }
}
