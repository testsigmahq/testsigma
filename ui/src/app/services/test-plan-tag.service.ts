import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {Pageable} from "../shared/models/pageable";
import {TestPlanTag} from "../models/test-plan-tag.model";

@Injectable({
  providedIn: 'root'
})
export class TestPlanTagService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public find(id: Number): Observable<TestPlanTag[]> {
    return this.http.get<TestPlanTag[]>(this.URLConstants.testPlanTagsUrl + "/associate_item/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data.map(tag => new TestPlanTag().deserialize(tag))),
      catchError(() => throwError('Problem while fetching TestPlanTag'))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<TestPlanTag[]> {
    return this.http.get<TestPlanTag[]>(this.URLConstants.testPlanTagsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => data.map(tag => new TestPlanTag().deserialize(tag))),
      catchError(() => throwError('Problem while fetching TestPlanTag'))
    )
  }

  public update(id: Number, tagsList: String[]): Observable<void> {
    return this.http.post<void>(this.URLConstants.testPlanTagsUrl + "/associate_item/" + id,
      tagsList, {
        headers: this.httpHeaders.contentTypeApplication
      }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while fetching selected Tags'))
    )
  }
}
