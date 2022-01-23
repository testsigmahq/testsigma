import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {ElementFilter} from "../models/element-filter.model";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";

@Injectable({
  providedIn: 'root'
})
export class ElementFilterService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(versionId: Number, filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<ElementFilter>> {
    return this.http.get<Page<ElementFilter>>(this.URLConstants.elementFilterUrl + "?versionId=" + versionId, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<ElementFilter>().deserialize(data, ElementFilter)),
      catchError(() => throwError('Problem while fetching ElementFilters'))
    )
  }

  public show(id: Number): Observable<ElementFilter> {
    return this.http.get<ElementFilter>(this.URLConstants.elementFilterUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new ElementFilter().deserialize(data)),
      catchError(() => throwError('Problem while fetching ElementFilter'))
    )
  }

  create(elementFilter: ElementFilter): Observable<ElementFilter> {
    return this.http.post<ElementFilter>(this.URLConstants.elementFilterUrl, elementFilter.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new ElementFilter().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  update(id: number, elementFilter: ElementFilter): Observable<ElementFilter> {
    return this.http.put<ElementFilter>(this.URLConstants.elementFilterUrl + "/" + id, elementFilter.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new ElementFilter().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  destroy(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.elementFilterUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => console.log(data)),
      catchError(() => throwError('Problem while deleting ElementFilter'))
    )
  }
}
