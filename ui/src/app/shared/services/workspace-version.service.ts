/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {Observable} from 'rxjs/internal/Observable';
import {map} from 'rxjs/internal/operators/map';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/internal/operators/catchError';
import {Pageable} from "../models/pageable";
import {Page} from "../models/page";

@Injectable({
  providedIn: 'root'
})
export class WorkspaceVersionService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<WorkspaceVersion>> {
    return this.http.get<WorkspaceVersion>(this.URLConstants.workspaceVersionsURL, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<WorkspaceVersion>().deserialize(data, WorkspaceVersion)),
      catchError(() => throwError('Problem while fetching ApplicationVersions'))
    )
  }

  public show(id: Number): Observable<WorkspaceVersion> {
    return this.http.get<WorkspaceVersion>(this.URLConstants.workspaceVersionsURL + '/' + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new WorkspaceVersion().deserialize(data)),
      catchError((exc) => throwError(exc))
    );
  }

  public create(version: WorkspaceVersion):  Observable<WorkspaceVersion>  {
    return this.http.post<WorkspaceVersion>(this.URLConstants.workspaceVersionsURL, version.serialize()).pipe(
      map(data => new WorkspaceVersion().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public clone(version: WorkspaceVersion):  Observable<WorkspaceVersion>  {
    return this.http.post<WorkspaceVersion>(this.URLConstants.workspaceVersionsURL + '/clone', version.serialize()).pipe(
      map(data => new WorkspaceVersion().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public update(version: WorkspaceVersion):  Observable<WorkspaceVersion>  {
    return this.http.put<WorkspaceVersion>(this.URLConstants.workspaceVersionsURL + '/' + version.id, version.serialize()).pipe(
      map(data => new WorkspaceVersion().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public destroy(id): Observable<void> {
    return this.http.delete<WorkspaceVersion>(this.URLConstants.workspaceVersionsURL + "/" + id )
      .pipe(
        map(data => console.log(data)),
        catchError(() => throwError('Problem while deleting Workspace Version::' + id))
      )
  }

}
