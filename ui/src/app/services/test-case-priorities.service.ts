import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Observable} from "rxjs/internal/Observable";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {TestCasePriority} from "../models/test-case-priority.model";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";

@Injectable({
  providedIn: 'root'
})
export class TestCasePrioritiesService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<TestCasePriority>> {
    return this.http.get<Page<TestCasePriority>>(this.URLConstants.testCasePrioritiesUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCasePriority>().deserialize(data, TestCasePriority)),
      catchError(() => throwError('Problem while fetching TestCasePriorities'))
    )
  }

  public show(id: Number): Observable<TestCasePriority> {
    return this.http.get<TestCasePriority>(this.URLConstants.testCasePrioritiesUrl + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestCasePriority().deserialize(data)),
      catchError((error) => {
        console.log(error)
        return throwError('Problem while fetching testcase priority::' + error)
      })
    );
  }

  public destroy(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testCasePrioritiesUrl + '/' + id).pipe(
      catchError((err) => throwError(err))
    );
  }

  public create(testCasePriority: TestCasePriority): Observable<TestCasePriority> {
    return this.http.post<TestCasePriority>(this.URLConstants.testCasePrioritiesUrl, testCasePriority)
      .pipe(
        map(data => new TestCasePriority().deserialize(data)),
        catchError((err) => throwError(err))
      )
  }

  public update(testCasePriority: TestCasePriority): Observable<TestCasePriority> {
    return this.http.put<TestCasePriority>(this.URLConstants.testCasePrioritiesUrl + "/" + testCasePriority.id, testCasePriority)
      .pipe(
        map(data => new TestCasePriority().deserialize(data)),
        catchError((err) => throwError(err))
      )
  }

}
