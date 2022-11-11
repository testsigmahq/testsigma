import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {EntityExternalMapping} from "../models/entity-external-mapping.model";
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

  public show(id: Number): Observable<EntityExternalMapping> {
    return this.http.get<EntityExternalMapping>(this.URLConstants.externalMappingsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new EntityExternalMapping().deserialize(data)),
      catchError(() => throwError('Problem while fetching EntityExternalMapping'))
    )
  }

  public findByTestCaseResult(testCaseResult: TestCaseResult): Observable<EntityExternalMapping[]> {
    return this.http.get<EntityExternalMapping[]>(this.URLConstants.externalMappingsUrl + "?test_case_result_id=" + testCaseResult.id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        let returnVal: EntityExternalMapping[] = [];
        data.forEach(res => returnVal.push(new EntityExternalMapping().deserialize(res)));
        return returnVal;
      }),
      catchError(() => throwError('Problem while fetching TestCaseResultExternalMappings'))
    )
  }

  public destroy(testCaseResultExternalMapping: EntityExternalMapping): Observable<void> {
    return this.http.delete<void>(this.URLConstants.externalMappingsUrl + "/" + testCaseResultExternalMapping.id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting TestCaseResultExternalMappings'))
    )
  }

  public create(testCaseResultExternalMapping: EntityExternalMapping): Observable<EntityExternalMapping> {
    return this.http.post<EntityExternalMapping>(this.URLConstants.externalMappingsUrl, testCaseResultExternalMapping.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new EntityExternalMapping().deserialize(data)),
      catchError(() => throwError('Problem while creating EntityExternalMapping'))
    )
  }

}
