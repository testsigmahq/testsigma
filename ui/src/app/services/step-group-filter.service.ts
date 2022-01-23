import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Observable, throwError} from "rxjs";
import {Page} from "../shared/models/page";
import {catchError, map} from "rxjs/operators";
import {TestCaseFilter} from "../models/test-case-filter.model";
import {StepGroupFilter} from "../models/step-group-filter.model";

@Injectable({
  providedIn: 'root'
})
export class StepGroupFilterService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(versionId: Number, filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<StepGroupFilter>> {
    return this.http.get<Page<StepGroupFilter>>(this.URLConstants.stepGroupFilterUrl + "?versionId=" + versionId, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<StepGroupFilter>().deserialize(data, StepGroupFilter)),
      catchError(() => throwError('Problem while fetching StepGroupFilter'))
    )
  }

  public show(id: Number): Observable<StepGroupFilter> {
    return this.http.get<StepGroupFilter>(this.URLConstants.stepGroupFilterUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new StepGroupFilter().deserialize(data)),
      catchError(() => throwError('Problem while fetching StepGroupFilter'))
    )
  }

  save(id: number, filter: StepGroupFilter): Observable<StepGroupFilter> {
    return this.http.put<StepGroupFilter>(this.URLConstants.stepGroupFilterUrl + "/" + id, filter.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new StepGroupFilter().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  create(filter: StepGroupFilter): Observable<StepGroupFilter> {
    return this.http.post<StepGroupFilter>(this.URLConstants.stepGroupFilterUrl, filter.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new StepGroupFilter().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  destroy(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.stepGroupFilterUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => console.log(data)),
      catchError(() => throwError('Problem while deleting StepGroupFilter'))
    )
  }
}
