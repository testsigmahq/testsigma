import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Observable, throwError} from "rxjs";
import {Page} from "../shared/models/page";
import {catchError, map} from "rxjs/operators";
import {TestCaseFilter} from "../models/test-case-filter.model";

@Injectable({
  providedIn: 'root'
})
export class TestCaseFilterService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(versionId: Number, filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<TestCaseFilter>> {
    return this.http.get<Page<TestCaseFilter>>(this.URLConstants.testCaseFilterUrl + "?versionId=" + versionId, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCaseFilter>().deserialize(data, TestCaseFilter)),
      catchError(() => throwError('Problem while fetching TestCaseFilters'))
    )
  }

  public show(id: Number): Observable<TestCaseFilter> {
    return this.http.get<TestCaseFilter>(this.URLConstants.testCaseFilterUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseFilter().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestCaseFilter'))
    )
  }

  save(id: number, filter: TestCaseFilter): Observable<TestCaseFilter> {
    return this.http.put<TestCaseFilter>(this.URLConstants.testCaseFilterUrl + "/" + id, filter.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseFilter().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  create(filter: TestCaseFilter): Observable<TestCaseFilter> {
    return this.http.post<TestCaseFilter>(this.URLConstants.testCaseFilterUrl, filter.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestCaseFilter().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  destroy(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testCaseFilterUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => console.log(data)),
      catchError(() => throwError('Problem while deleting TestCaseFilter'))
    )
  }
}
