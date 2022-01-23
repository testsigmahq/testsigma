import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestCaseResult} from "../models/test-case-result.model";
import {DataSourceService} from "../shared/services/data-source.service";

@Injectable({
  providedIn: 'root'
})
export class TestCaseResultService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<TestCaseResult>> {
    return this.http.get<Page<TestCaseResult>>(this.URLConstants.testCaseResultsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCaseResult>().deserialize(data, TestCaseResult)),
      catchError(() => throwError('Problem while fetching TestCaseResults'))
    )
  }

  public show(id: Number): Observable<TestCaseResult> {
    return this.http.get<TestCaseResult>(this.URLConstants.testCaseResultsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseResult().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestCaseResult'))
    )
  }

  public exportScreenshots(id: Number): Observable<void> {
    return this.http.put<void>(this.URLConstants.testCaseResultsUrl + "/" + id + "/export_screenshots", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      catchError(() => throwError('Problem while exporting Screenshots TestCaseResult'))
    )
  }

  public update(testCaseResult: TestCaseResult): Observable<TestCaseResult> {
    return this.http.put<TestCaseResult>(this.URLConstants.testCaseResultsUrl + "/" + testCaseResult.id, testCaseResult.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseResult().deserialize(data)),
      catchError(() => throwError('Problem while update TestCaseResult'))
    )
  }
}
