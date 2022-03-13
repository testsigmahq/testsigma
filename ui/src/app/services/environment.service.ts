import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Environment} from "../models/environment.model";
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<Environment>> {
    return this.http.get<Page<Environment>>(this.URLConstants.environmentsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => { return new Page<Environment>().deserialize(data, Environment)}),
      catchError(() => throwError('Problem while fetching Environment'))
    )
  }

  public show(id: Number): Observable<Environment> {
    return this.http.get<Environment>(this.URLConstants.environmentsUrl + "/" + id , {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new Environment().deserialize(data)),
      catchError(() => throwError('Problem while fetching Environment'))
    )
  }

  public create(environment: Environment ) : Observable<Environment> {
    return this.http.post<Environment>(this.URLConstants.environmentsUrl, environment).pipe(
      map(data => new Environment().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  public update(environment: Environment): Observable<any> {
    return this.http.put(this.URLConstants.environmentsUrl + "/"+ environment.id , environment.serialize()).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    )
  }

  public delete(id: any): Observable<void> {
    return this.http.delete<void>(this.URLConstants.environmentsUrl + '/' + id,
      {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    )
  }

  public bulkDestroy(ids: any[]): Observable<void> {
    let params = new HttpParams().set("ids[]", ids.toString());
    return this.http.delete<void>(this.URLConstants.environmentsUrl + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

}
