import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestCaseResultExternalMapping} from "../models/test-case-result-external-mapping.model";
import {TestCaseResult} from "../models/test-case-result.model";

@Injectable({
  providedIn: 'root'
})
export class TestCaseResultExternalMappingService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public show(id: Number): Observable<TestCaseResultExternalMapping> {
    return this.http.get<TestCaseResultExternalMapping>(this.URLConstants.externalMappingsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseResultExternalMapping().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestCaseResultExternalMapping'))
    )
  }

  public findByTestCaseResult(testCaseResult: TestCaseResult): Observable<TestCaseResultExternalMapping[]> {
    return this.http.get<TestCaseResultExternalMapping[]>(this.URLConstants.externalMappingsUrl + "?test_case_result_id=" + testCaseResult.id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        let returnVal: TestCaseResultExternalMapping[] = [];
        data.forEach(res => returnVal.push(new TestCaseResultExternalMapping().deserialize(res)));
        return returnVal;
      }),
      catchError(() => throwError('Problem while fetching TestCaseResultExternalMappings'))
    )
  }

  public destroy(testCaseResultExternalMapping: TestCaseResultExternalMapping): Observable<void> {
    return this.http.delete<void>(this.URLConstants.externalMappingsUrl + "/" + testCaseResultExternalMapping.id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting TestCaseResultExternalMappings'))
    )
  }

  public create(testCaseResultExternalMapping: TestCaseResultExternalMapping): Observable<TestCaseResultExternalMapping> {
    return this.http.post<TestCaseResultExternalMapping>(this.URLConstants.externalMappingsUrl, testCaseResultExternalMapping.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseResultExternalMapping().deserialize(data)),
      catchError(() => throwError('Problem while creating TestCaseResultExternalMapping'))
    )
  }

}
