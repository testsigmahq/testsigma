import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {Workspace} from "../models/workspace.model";

@Injectable({
  providedIn: 'root'
})
export class WorkspaceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public show(id: Number): Observable<Workspace> {
    return this.http.get<Workspace>(this.URLConstants.workspacesUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new Workspace().deserialize(data)),
      catchError(() => throwError('Problem while fetching Workspace'))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<Workspace>> {
    return this.http.get<Workspace>(this.URLConstants.workspacesUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<Workspace>().deserialize(data, Workspace)),
      catchError(() => throwError('Problem while fetching Applications'))
    )
  }

  public create(application: Workspace): Observable<Workspace> {
    return this.http.post<Workspace>(this.URLConstants.workspacesUrl, application.serialize())
      .pipe(
        map(data => new Workspace().deserialize(data)),
        catchError((err) => throwError(err))
      )
  }

  public update(application: Workspace): Observable<Workspace> {
    return this.http.put<Workspace>(this.URLConstants.workspacesUrl + "/" + application.id , application.serialize())
      .pipe(
        map(data =>new Workspace().deserialize(data)),
        catchError((err) => throwError(err))
      )
  }

  public delete(id): Observable<void> {
    return this.http.delete<void>(this.URLConstants.workspacesUrl + "/" + id )
      .pipe(
        map(data => console.log(data)),
        catchError(() => throwError('Problem while deleting Workspace'))
      )
  }

}
