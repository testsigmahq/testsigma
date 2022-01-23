import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Pageable} from "../shared/models/pageable";
import {ElementTag} from "../models/element-tag.model";

@Injectable({
  providedIn: 'root'
})
export class ElementTagService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<ElementTag[]> {
    return this.http.get<ElementTag[]>(this.URLConstants.elementTagsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => data.map(tag => new ElementTag().deserialize(tag))),
      catchError(() => throwError('Problem while fetching Element Tags'))
    )
  }

  public find(elementId): Observable<ElementTag[]> {
    return this.http.get<ElementTag[]>(this.URLConstants.elementTagsUrl + "/associate_item/" + elementId, {
      headers: this.httpHeaders.contentTypeApplication,
    }).pipe(
      map(data => data.map(tag => new ElementTag().deserialize(tag))),
      catchError(() => throwError('Problem while fetching selected Tags'))
    )
  }

  public update(elementId, tagsList: String[]): Observable<void> {
    return this.http.post<void>(this.URLConstants.elementTagsUrl + "/associate_item/" + elementId,
      tagsList, {
        headers: this.httpHeaders.contentTypeApplication
      }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while fetching selected Tags'))
    )
  }


}
