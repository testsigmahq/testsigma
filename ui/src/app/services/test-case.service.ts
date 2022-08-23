import {EventEmitter, Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {TestCase} from "../models/test-case.model";
import {Observable, Subject, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient, HttpParams} from '@angular/common/http';
import {FilterableDataSourceService} from "../shared/services/filterable-data-source.service";
import {TestCaseCoverageSummary} from "../models/test-case-coverage-summary.model";
import {ByStatusCount} from "../models/by-status-count.model";
import {ByTypeCount} from "../models/by-type-count.model";

@Injectable({
  providedIn: 'root'
})
export class TestCaseService implements FilterableDataSourceService {
  public refeshTestCaseAfterSaveOrUpdate:Subject<boolean> = new Subject<boolean>();

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }
  public refresh: Subject<number|null> = new Subject<number|null>();
  public stepsFetch: EventEmitter<any> = new EventEmitter();
  public emitStepLength(preferenceName: any) {
    this.stepsFetch.emit(preferenceName);
  }
  public getStepLengthEmitter() {
    return this.stepsFetch;
  }

  public filter(filterId?: number, versionId?: number, pageable?: Pageable, sortBy?: string): Observable<Page<TestCase>> {
    return this.http.get<Page<TestCase>>(this.URLConstants.testCasesUrl + "/filter/" + filterId + "?versionId=" + versionId, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(undefined, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCase>().deserialize(data, TestCase)),
      catchError(() => throwError('Problem while fetching TestCases'))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestCase>> {
    return this.http.get<Page<TestCase>>(this.URLConstants.testCasesUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCase>().deserialize(data, TestCase)),
      catchError(() => throwError('Problem while fetching TestCases'))
    )
  }

  public show(id: number): Observable<TestCase> {
    return this.http.get<TestCase>(this.URLConstants.testCasesUrl + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestCase().deserialize(data)),
      catchError(() => throwError('Problem while getting Test Case::' + id))
    );
  }

  public coverageSummary(versionId: number): Observable<TestCaseCoverageSummary> {
    return this.http.get<TestCaseCoverageSummary>(this.URLConstants.testCasesUrl + "/coverage_summary?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestCaseCoverageSummary().deserialize(data)),
      catchError(() => throwError('Problem while getting Test Case::CoverageSummary'))
    );
  }

  public byStatusCount(versionId: number): Observable<ByStatusCount[]> {
    return this.http.get<ByStatusCount[]>(this.URLConstants.testCasesUrl + "/break_up_by_status?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data.map(byStatus => new ByStatusCount().deserialize(byStatus))),
      catchError(() => throwError('Problem while getting Test Case::CoverageSummary'))
    );
  }

  public byTypeCount(versionId: number): Observable<ByTypeCount[]> {
    return this.http.get<ByTypeCount[]>(this.URLConstants.testCasesUrl + "/break_up_by_type?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data.map(byStatus => new ByTypeCount().deserialize(byStatus))),
      catchError(() => throwError('Problem while getting Test Case::CoverageSummary'))
    );
  }

  public update(testcase: TestCase): Observable<TestCase> {
    return this.http.put<TestCase>(this.URLConstants.testCasesUrl + "/" + testcase.id, testcase.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCase().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testCasesUrl + '/' + id);
  }

  public markAsDeleted(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testCasesUrl + '/' + id + '/mark_as_delete');
  }

  public bulkMarkAsDeleted(ids: number[]): Observable<void> {
    let params = new HttpParams().set("ids", ids.toString());
    return this.http.delete<void>(this.URLConstants.testCasesUrl+'/mark_as_delete', {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    })
  }

  public create(testCase: TestCase): Observable<TestCase> {
    return this.http.post<TestCase>(this.URLConstants.testCasesUrl, testCase.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCase().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  restore(testCaseId: number): Observable<void> {
    return this.http.put<void>(this.URLConstants.testCasesUrl + '/' + testCaseId + '/restore', {}).pipe(
      map(_data => {}),
      catchError((error) => throwError(error))
    );
  }

  copy(copyRequest: { name: string; stepIds?: number[]; testCaseId: number,  isStepGroup: boolean}) : Observable<TestCase>{
    return this.http.post<TestCase>(this.URLConstants.testCasesUrl+"/copy", copyRequest, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCase().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public validateNavigationUrls(testCaseId: number, currentUrl?: string): Observable<string[]> {
    let params;
    if(currentUrl)
      params = new HttpParams().set("currentUrl", currentUrl.toString());
    return this.http.get<string[]>(this.URLConstants.testCasesUrl + "/validateUrls/" + testCaseId , {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while Validating Urls', error);
      })

    )
  }

}
