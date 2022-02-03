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
import {TestsigmaOSConfig} from "../models/testsigma-os-config.model";
import {Onboarding} from "../../models/onboarding.model";

@Injectable()
export class TestsigmaOsConfigService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  show() {
    return this.http.get<TestsigmaOSConfig>(this.URLConstants.testsigmaOSConfigURL, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  create(openSource: TestsigmaOSConfig) {
    return this.http.post<TestsigmaOSConfig>(this.URLConstants.testsigmaOSConfigURL, openSource.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  getOTP(onboarding: Onboarding) {
    return this.http.post<void>(this.URLConstants.testsigmaOSConfigURL+"/otp", onboarding.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  activate(otp: string) {
    return this.http.get<void>(this.URLConstants.testsigmaOSConfigURL+"/activate/"+otp, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }
}
