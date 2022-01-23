import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {DryTestPlan} from "../models/dry-test-plan.model";
import {Pageable} from "../shared/models/pageable";
import {TestPlanResult} from "../models/test-plan-result.model";

@Injectable({
  providedIn: 'root'
})
export class DryTestPlanService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public create(execution: DryTestPlan): Observable<TestPlanResult> {
    return this.http.post<TestPlanResult>(this.URLConstants.dryTestPlansUrl, execution.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestPlanResult().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<DryTestPlan>> {
    return this.http.get<Page<DryTestPlan>>(this.URLConstants.dryTestPlansUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<DryTestPlan>().deserialize(data, DryTestPlan)),
      catchError(() => throwError('Problem while fetching dry Execution'))
    )
  }
}
