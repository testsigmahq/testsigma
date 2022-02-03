import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Observable} from "rxjs/internal/Observable";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {TestCaseType} from "../models/test-case-type.model";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";

@Injectable({
  providedIn: 'root'
})
export class TestCaseTypesService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<TestCaseType>> {
    return this.http.get<Page<TestCaseType>>(this.URLConstants.testCaseTypesUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCaseType>().deserialize(data, TestCaseType)),
      catchError(() => throwError('Problem while fetching TestCaseTypes'))
    )
  }

  public show(id: Number): Observable<TestCaseType> {
    return this.http.get<TestCaseType>(this.URLConstants.testCaseTypesUrl + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestCaseType().deserialize(data)),
      catchError((error) => {
        return throwError('Problem while fetching testcase priority::' + error)
      })
    );
  }

  public destroy(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testCaseTypesUrl + '/' + id).pipe(
      catchError((err) => throwError(err))
    );
  }

  public create(testCaseType: TestCaseType): Observable<TestCaseType> {
    return this.http.post<TestCaseType>(this.URLConstants.testCaseTypesUrl, testCaseType)
      .pipe(
        map(data => new TestCaseType().deserialize(data)),
        catchError((err) => throwError(err))
      )
  }

  public update(testCaseType: TestCaseType): Observable<TestCaseType> {
    return this.http.put<TestCaseType>(this.URLConstants.testCaseTypesUrl + "/" + testCaseType.id, testCaseType)
      .pipe(
        map(data => new TestCaseType().deserialize(data)),
        catchError((err) => throwError(err))
      )
  }

}
