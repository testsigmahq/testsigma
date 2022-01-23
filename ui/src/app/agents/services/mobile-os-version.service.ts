/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {HttpHeadersService} from "../../shared/services/http-headers.service";
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {Observable} from 'rxjs/internal/Observable';
import {MobileOsVersion} from "../models/mobile-os-version.model";
import {map} from 'rxjs/internal/operators/map';
import {throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MobileOsVersionService {
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService) {
  }

  public findAll(workspaceType: WorkspaceType): Observable<MobileOsVersion[]> {
    return this.http.get<MobileOsVersion[]>(
      "/platforms/mobile/devices?workspaceType=" + workspaceType,
      {
        headers: this.httpHeaders.contentTypeApplication
      })
      .pipe(map((data) => {
          let mobileVersions: MobileOsVersion[] = [];
          data.forEach((mobileOsVersion) => mobileVersions.push(new MobileOsVersion().deserialize(mobileOsVersion)));
          return mobileVersions;
        }),
        catchError((err) => {
          return throwError('Problem while fetching Agent Devices', err)
        })
      );
  }
}
