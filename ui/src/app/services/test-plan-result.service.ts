import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestPlanResult} from "../models/test-plan-result.model";
import {DataSourceService} from "../shared/services/data-source.service";

@Injectable({
  providedIn: 'root'
})
export class TestPlanResultService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestPlanResult>> {
    return this.http.get<Page<TestPlanResult>>(this.URLConstants.testPlanResultsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestPlanResult>().deserialize(data, TestPlanResult)),
      catchError(() => throwError('Problem while fetching Execution Results'))
    )
  }

  public getRunningCounts(): Observable<Page<TestPlanResult>> {
    return this.http.get<Page<TestPlanResult>>(this.URLConstants.testPlanResultsUrl+"/running-counts", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new Page<TestPlanResult>().deserialize(data, TestPlanResult)),
      catchError(() => throwError('Problem while fetching Execution Results'))
    )
  }

  public show(id: Number): Observable<TestPlanResult> {
    return this.http.get<TestPlanResult>(this.URLConstants.testPlanResultsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestPlanResult().deserialize(data)),
      catchError(() => throwError('Problem while fetching Execution Result'))
    )
  }

  public update(testPlanResult: TestPlanResult): Observable<TestPlanResult> {
    return this.http.put<TestPlanResult>(this.URLConstants.testPlanResultsUrl + "/" + testPlanResult.id, testPlanResult.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map(data => new TestPlanResult().deserialize(data)),
      catchError(() => throwError('Problem while updating Execution Result'))
    )
  }

  public create(testPlanResult: TestPlanResult): Observable<TestPlanResult> {
    return this.http.post<TestPlanResult>(this.URLConstants.testPlanResultsUrl, testPlanResult.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map(data => new TestPlanResult().deserialize(data)),
      catchError((err) => {
        return throwError(err.error && err.error.code ? err.error.code : 'Problem while start Execution')
      })
    )
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testPlanResultsUrl + '/' + id);
  }
}
