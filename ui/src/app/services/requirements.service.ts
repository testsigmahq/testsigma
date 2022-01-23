import { Injectable } from '@angular/core';
import {Pageable} from "../shared/models/pageable";
import {Observable} from "rxjs/internal/Observable";
import {Page} from "../shared/models/page";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {HttpClient, HttpParams} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {TestData} from "../models/test-data.model";
import {Requirement} from "../models/requirement.model";
@Injectable({
  providedIn: 'root'
})
export class RequirementsService {
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<Requirement>> {
    return this.http.get<Page<Requirement>>(this.URLConstants.requirementsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<Requirement>().deserialize(data, Requirement)),
      catchError(() => throwError('Problem while fetching Requirements'))
    )
  }

  public show(id: Number): Observable<Requirement> {
    return this.http.get<Requirement>(this.URLConstants.requirementsUrl + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => {
        return  new Requirement().deserialize(data)
      }),
      catchError(() => throwError('Problem while fetching Requirements'))
    )
  }

  public delete(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.requirementsUrl + '/' + id,
      {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting Requirements'))
    )
  }

  public bulkDestroy(ids: number[]): Observable<void>{
    let params = new HttpParams().set("ids[]", ids.toString());
    return this.http.delete<void>(this.URLConstants.requirementsUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting Requirements'))
    );
  }

  public create( requirement: Requirement ): Observable<any> {
    let params = new HttpParams().set("versionId", requirement.workspaceVersionId.toString());
    return this.http.post(this.URLConstants.requirementsUrl, requirement.serialize(),
      {
        headers: this.httpHeaders.contentTypeApplication,
        params: params
      })
      .pipe(
        map(data => data),
        catchError((exception) => throwError(exception))
      )
  }


  public update( requirement: Requirement ): Observable<any> {
    return this.http.put(this.URLConstants.requirementsUrl + "/"+ requirement.id , requirement.serialize(),
      { headers: this.httpHeaders.contentTypeApplication })
      .pipe(
        map(data => data),
        catchError((exception) => throwError(exception))
      )
  }
}
