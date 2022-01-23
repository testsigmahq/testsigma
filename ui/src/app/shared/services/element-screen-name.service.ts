import {Injectable} from '@angular/core';
import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Element} from "../../models/element.model";
import {Observable} from 'rxjs/internal/Observable';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/internal/operators/catchError';
import {map} from 'rxjs/internal/operators/map';
import {Pageable} from "../models/pageable";
import {Page} from "../models/page";
import {FilterableDataSourceService} from "./filterable-data-source.service";
import {ElementScreenName} from "../../models/element-screen-name.model";

@Injectable({
  providedIn: 'root'
})
export class ElementScreenNameService {
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }
  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<ElementScreenName>> {
    return this.http.get<Page<ElementScreenName>>(this.URLConstants.elementScreenNameURL, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<ElementScreenName>().deserialize(data, ElementScreenName)),
      catchError(() => throwError('Problem while fetching Elements'))
    )
  }

  public create(element: ElementScreenName): Observable<ElementScreenName> {
    return this.http.post<ElementScreenName>(this.URLConstants.elementScreenNameURL, element.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new ElementScreenName().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }
}
