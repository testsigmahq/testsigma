import {Injectable} from '@angular/core';
import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Pageable} from "../models/pageable";
import {Page} from "../models/page";
import {Observable} from 'rxjs/internal/Observable';
import {map} from 'rxjs/internal/operators/map';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/internal/operators/catchError';
import {UploadVersion} from "../models/upload-version.model";

@Injectable({
  providedIn: 'root'
})
export class UploadVersionService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {

  }

  public findAll(query?: string, sortBy?: string, pageable?: Pageable): Observable<Page<UploadVersion>> {
    return this.http.get<Page<UploadVersion>>(this.URLConstants.uploadVersionsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(query,sortBy, pageable)
    }).pipe(
      map(data => new Page<UploadVersion>().deserialize(data, UploadVersion)),
      catchError(() => throwError('Problem while fetching uploads by upload type'))
    )
  }

  public find(id: Number): Observable<UploadVersion> {
    return this.http.get<UploadVersion>(this.URLConstants.uploadVersionsUrl + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new UploadVersion().deserialize(data)),
      catchError(() => throwError('Problem while fetching upload::' + id))
    );
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.uploadVersionsUrl + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting Upload::' + id))
    );
  }
}
