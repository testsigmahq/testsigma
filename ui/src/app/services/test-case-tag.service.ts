import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Pageable} from "../shared/models/pageable";
import {TestCaseTag} from "../models/test-case-tag.model";

@Injectable({
  providedIn: 'root'
})
export class TestCaseTagService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public find(id: Number): Observable<TestCaseTag[]> {
    return this.http.get<TestCaseTag[]>(this.URLConstants.testCaseTagsUrl + "/associate_item/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data.map(tag => new TestCaseTag().deserialize(tag))),
      catchError(() => throwError('Problem while fetching TestCaseTag'))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<TestCaseTag[]> {
    return this.http.get<TestCaseTag[]>(this.URLConstants.testCaseTagsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => data.map(tag => new TestCaseTag().deserialize(tag))),
      catchError(() => throwError('Problem while fetching TestCaseTag'))
    )
  }

  public update(id: Number, tagsList: String[]): Observable<void> {
    return this.http.post<void>(this.URLConstants.testCaseTagsUrl + "/associate_item/" + id,
      tagsList, {
        headers: this.httpHeaders.contentTypeApplication
      }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while fetching selected Tags'))
    )
  }

}
