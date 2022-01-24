/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {UrlConstantsService} from "app/shared/services/url.constants.service";
import {HttpHeadersService} from "app/shared/services/http-headers.service";
import {Observable} from 'rxjs/internal/Observable';
import {Session} from "app/shared/models/session.model";
import {throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";

@Injectable()
export class SessionService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  getSession() {
    return this.http.get<Session>(this.URLConstants.sessionUrl, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Session().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  login(username: string, password: string) {
    return this.http.post<any>(this.URLConstants.loginUrl, {
      username,
      password
    }, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(() => {
        return;
      })
    );
  }

  logout() {
    return this.http.get<any>(this.URLConstants.logoutUrl);
  }
}
