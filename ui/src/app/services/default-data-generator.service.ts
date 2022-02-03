import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {DefaultDataGenerator} from "../models/default-data-generator.model";

@Injectable({
  providedIn: 'root'
})
export class DefaultDataGeneratorService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public show(id: Number): Observable<DefaultDataGenerator> {
    return this.http.get<DefaultDataGenerator>(this.URLConstants.testDataFunctionsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new DefaultDataGenerator().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestDataFunctions'))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<DefaultDataGenerator>> {
    return this.http.get<DefaultDataGenerator>(this.URLConstants.testDataFunctionsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<DefaultDataGenerator>().deserialize(data, DefaultDataGenerator)),
      catchError(() => throwError('Problem while fetching TestDataFunctions'))
    )
  }
}
