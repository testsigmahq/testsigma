import {EventEmitter, Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {Observable, Subject, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Report} from "../models/report.model";

@Injectable({
  providedIn: 'root'
})
export class ReportsService{

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }


  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<any>> {
    return this.http.get<Page<any>>(this.URLConstants.reportsURL, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<any>().deserialize(data,Report)),
      catchError(() => throwError('Problem while fetching Reports'))
    )
  }

  public show(id: number): Observable<any> {
    return this.http.get<any>(this.URLConstants.reportsURL + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting reports::' + id))
    );
  }



  public generateReport(id: number): Observable<any> {
    return this.http.get<any>(this.URLConstants.reportsURL + "/generate_report/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting reports::' + id))
    );
  }

  public getFlakyTests(versionId: number): Observable<any[]> {
    return this.http.get<any[]>(this.URLConstants.reportsURL + "/flaky_tests?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting Flaky Tests'))
    );
  }

  public getRunDurationTrend(versionId: number): Observable<any[]> {
    return this.http.get<any[]>(this.URLConstants.reportsURL + "/run_duration_trend?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting run duration Trend'))
    );
  }

  public getTopFailures(versionId: number): Observable<any[]> {
    return this.http.get<any[]>(this.URLConstants.reportsURL + "/top_failures?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting Top Failures'))
    );
  }

  public getLingeredTests(versionId: number): Observable<any[]> {
    return this.http.get<any[]>(this.URLConstants.reportsURL + "/lingered_tests?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting Lingered tests'))
    );
  }

  public getFailuresByCategory(versionId: number): Observable<any[]> {
    return this.http.get<any[]>(this.URLConstants.reportsURL + "/failures_by_category?versionId=" + versionId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while getting failures by category'))
    );
  }

  public update(any: any): Observable<any> {
    return this.http.put<any>(this.URLConstants.reportsURL + "/" + any.id, any.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new any().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.reportsURL + '/' + id);
  }

  public create(any: any): Observable<any> {
    return this.http.post<any>(this.URLConstants.reportsURL, any, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

  public runQueryReport(any: any): Observable<any> {
    return this.http.post<any>(this.URLConstants.reportsURL+"/generate_query_report", any, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

}
