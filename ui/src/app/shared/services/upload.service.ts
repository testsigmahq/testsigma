import {Injectable} from '@angular/core';
import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Pageable} from "../models/pageable";
import {Page} from "../models/page";
import {Upload} from "../models/upload.model";
import {Observable} from 'rxjs/internal/Observable';
import {map} from 'rxjs/internal/operators/map';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/internal/operators/catchError';
import {Element} from "../../models/element.model";

@Injectable({
  providedIn: 'root'
})
export class UploadService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {

  }

  public findAll(query?: string, sortBy?: string, pageable?: Pageable): Observable<Page<Upload>> {
    return this.http.get<Page<Upload>>(this.URLConstants.uploadsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(query,sortBy, pageable)
    }).pipe(
      map(data => new Page<Upload>().deserialize(data, Upload)),
      catchError(() => throwError('Problem while fetching uploads by upload type'))
    )
  }

  public find(id: Number | String): Observable<Upload> {
    return this.http.get<Upload>(this.URLConstants.uploadsUrl + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Upload().deserialize(data)),
      catchError(() => throwError('Problem while fetching upload::' + id))
    );
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.uploadsUrl + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting Upload::' + id))
    );
  }

  public bulkDestroy(ids: number[]): Observable<void> {
    let params = new HttpParams().set("ids[]", ids.toString());
    return this.http.delete<void>(this.URLConstants.uploadsUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    });
  }

  public save(id: number, formData: FormData): Observable<Upload>  {
    return this.http.post<Upload>(this.URLConstants.uploadsUrl + "/"+id, formData, {
    }).pipe(
      map(data => new Upload().deserialize(data)),
      catchError(() => throwError('Problem while uploading file'))
    )
  }

  public create(formData: FormData): Observable<Upload>  {
    return this.http.post<Upload>(this.URLConstants.uploadsUrl , formData, {
    }).pipe(
      map(data => new Upload().deserialize(data)),
      catchError((exception) => throwError(exception))
    )
  }
}
