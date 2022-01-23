import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestPlan} from "../models/test-plan.model";

@Injectable({
  providedIn: 'root'
})
export class TestPlanService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestPlan>> {
    return this.http.get<Page<TestPlan>>(this.URLConstants.testPlansUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestPlan>().deserialize(data, TestPlan)),
      catchError(() => throwError('Problem while fetching Execution not found'))
    )
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testPlansUrl + '/' + id);
  }

  public bulkDestroy(ids: any[]): Observable<void> {
    let params = new HttpParams().set("ids[]", ids.toString());
    return this.http.delete<void>(this.URLConstants.testPlansUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting Test Plans'))
    );
  }

  find(id: number): Observable<TestPlan> {
    return this.http.get<TestPlan>(this.URLConstants.testPlansUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestPlan().deserialize(data)),
      catchError(() => throwError('Problem while fetching Execution not found'))
    )
  }

  update(execution: TestPlan): Observable<TestPlan> {
    return this.http.put<TestPlan>(this.URLConstants.testPlansUrl + "/" + execution.id, execution.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestPlan().deserialize(data)),
      catchError((exception) => throwError(exception))
    )
  }

  create(execution: TestPlan): Observable<TestPlan> {
    return this.http.post<TestPlan>(this.URLConstants.testPlansUrl, execution.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestPlan().deserialize(data)),
      catchError((exception) => throwError(exception))
    )
  }

}
