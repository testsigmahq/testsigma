import {EventEmitter, Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {Observable, Subject, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient, HttpParams} from '@angular/common/http';

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
      map(data => new Page<any>()),
      catchError(() => throwError('Problem while fetching Reports'))
    )
  }

  public show(id: number): Observable<any> {
    return this.http.get<any>(this.URLConstants.reportsURL + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
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
      catchError(() => throwError('Problem while getting Test Case::CoverageSummary'))
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
    return this.http.post<any>(this.URLConstants.reportsURL, any.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new any().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

}
