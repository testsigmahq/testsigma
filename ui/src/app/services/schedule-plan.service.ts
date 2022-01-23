import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Observable} from "rxjs/internal/Observable";
import {Page} from "../shared/models/page";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {SchedulePlan} from "../models/schedule-plan.model";
import {DataSourceService} from "../shared/services/data-source.service";

@Injectable({
  providedIn: 'root'
})
export class SchedulePlanService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<SchedulePlan>> {
    return this.http.get<Page<SchedulePlan>>(this.URLConstants.scheduledPlanUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<SchedulePlan>().deserialize(data, SchedulePlan)),
      catchError(() => throwError('Problem while fetching Scheduled plan'))
    )
  }

  public show(id: Number): Observable<SchedulePlan> {
    return this.http.get<SchedulePlan>(this.URLConstants.scheduledPlanUrl + '/' + id,
      {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new SchedulePlan().deserialize(data)),
      catchError((error) => {
        return throwError('Problem while fetching scheduled plan::' + error)
      })
    );
  }


  public create(scheduledPlan: SchedulePlan): Observable<SchedulePlan> {
    return this.http.post<SchedulePlan>(this.URLConstants.scheduledPlanUrl,
      scheduledPlan.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new SchedulePlan().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public update(id: number, scheduledPlan: SchedulePlan): Observable<SchedulePlan> {
    return this.http.put<SchedulePlan>(this.URLConstants.scheduledPlanUrl + "/" + id,
      scheduledPlan.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new SchedulePlan().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.scheduledPlanUrl + "/" + id)
      .pipe(
        map(data => data),
        catchError(() => throwError('Problem while deleting Schedule::' + id))
      );
  }

}
