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
import {map} from 'rxjs/internal/operators/map';
import {throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Platform} from "../models/platform.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {PlatformOsVersion} from "../models/platform-os-version.model";
import {PlatformBrowser} from "../models/platform-browser.model";
import {PlatformScreenResolution} from "../models/platform-screen-resolution.model";
import {PlatformBrowserVersion} from "../models/platform-browser-version.model";
import {CloudDevice} from "../models/cloud-device.model";

@Injectable({
  providedIn: 'root'
})
export class PlatformService {
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService) {
  }

  public findAll(workspaceType: WorkspaceType, testPlanLabType: TestPlanLabType): Observable<Platform[]> {
    return this.http.get<Platform[]>("/platforms?workspaceType=" + workspaceType + "&testPlanLabType=" + testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        let platforms: Platform[] = [];
        data.forEach((platform) => platforms.push(new Platform().deserialize({id: platform, name: platform})));
        return platforms;
      }),
      catchError((err) => {
        return throwError('Problem while fetching platforms', err)
      })
    );
  }

  public findAllOsVersions(platform: Platform, workspaceType: WorkspaceType, testPlanLabType: TestPlanLabType): Observable<PlatformOsVersion[]> {
    return this.http.get<PlatformOsVersion[]>("/platforms/"+platform.id+"/os_versions?workspaceType="+workspaceType+"&testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        let osVersions: PlatformOsVersion[] = [];
        data.forEach((osVersion) => osVersions.push(new PlatformOsVersion().deserialize(osVersion)));
        return osVersions;
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform OS versions', err)
      })
    );
  }

  public findOsVersion(platformOsVersionId: Number, testPlanLabType: TestPlanLabType):Observable<PlatformOsVersion>{
    return this.http.get<PlatformOsVersion>("/platforms/"+platformOsVersionId+"/os_version?testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        return new PlatformOsVersion().deserialize(data);
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform OS versions', err)
      })
    );
  }

  public findAllBrowsers(platform: Platform, osVersion: PlatformOsVersion, workspaceType: WorkspaceType, testPlanLabType: TestPlanLabType): Observable<PlatformBrowser[]> {
    return this.http.get<PlatformBrowser[]>("/platforms/"+platform.id+"/"+osVersion?.version+"/browsers?workspaceType="+workspaceType+"&testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        let browsers: PlatformBrowser[] = [];
        data.forEach((browser) => browsers.push(new PlatformBrowser().deserialize({name: browser, id: (browser+"").toUpperCase()})));
        return browsers;
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform Browsers versions', err)
      })
    );
  }

  public findAllScreenResolutions(platform: Platform, osVersion: PlatformOsVersion, workspaceType: WorkspaceType, testPlanLabType: TestPlanLabType): Observable<PlatformScreenResolution[]> {
    return this.http.get<PlatformScreenResolution[]>("/platforms/"+platform.id+"/"+osVersion?.version+"/screen_resolutions?workspaceType="+workspaceType+"&testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        let resolutions: PlatformScreenResolution[] = [];
        data.forEach((resolution) => resolutions.push(new PlatformScreenResolution().deserialize(resolution)));
        return resolutions;
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform Browsers versions', err)
      })
    );
  }

  public findScreenResolution(platformScreenResolutionId: Number, testPlanLabType: TestPlanLabType):Observable<PlatformScreenResolution>{
    return this.http.get<PlatformScreenResolution>("/platforms/"+platformScreenResolutionId+"/screen_resolution?testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        return new PlatformScreenResolution().deserialize(data);
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform OS versions', err)
      })
    );
  }

  public findAllBrowserVersions(platform: Platform, osVersion: PlatformOsVersion, browser: PlatformBrowser, workspaceType: WorkspaceType, testPlanLabType: TestPlanLabType): Observable<PlatformBrowserVersion[]> {
    return this.http.get<PlatformBrowserVersion[]>("/platforms/"+platform.id+"/"+osVersion?.version+"/browser/"+browser?.name+"/versions?workspaceType="+workspaceType+"&testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        let browserVersions: PlatformBrowserVersion[] = [];
        data.forEach((browserVersion) => browserVersions.push(new PlatformBrowserVersion().deserialize(browserVersion)));
        return browserVersions;
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform Browsers versions', err)
      })
    );
  }

  public findBrowserVersion(platformBrowserVersionId: Number, testPlanLabType: TestPlanLabType):Observable<PlatformBrowserVersion>{
    return this.http.get<PlatformBrowserVersion>("/platforms/"+platformBrowserVersionId+"/browser_version?testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        return new PlatformBrowserVersion().deserialize(data);
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform OS versions', err)
      })
    );
  }


  findAllDevices(platform: Platform, platformOsVersion: PlatformOsVersion, workspaceType: WorkspaceType, testPlanLabType: TestPlanLabType): Observable<CloudDevice[]> {
    return this.http.get<CloudDevice[]>("/platforms/"+platform.id+"/"+platformOsVersion.version+"/devices?workspaceType="+workspaceType+"&testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map((data) => {
        let cloudDevices: CloudDevice[] = [];
        data.forEach((device) => cloudDevices.push(new CloudDevice().deserialize(device)));
        return cloudDevices;
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform Devices', err)
      })
    );
  }

  public findDevice(platformDeviceId: Number, testPlanLabType: TestPlanLabType):Observable<CloudDevice>{
    return this.http.get<CloudDevice>("/platforms/"+platformDeviceId+"/device?testPlanLabType="+testPlanLabType, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(map((data) => {
        return new CloudDevice().deserialize(data);
      }),
      catchError((err) => {
        return throwError('Problem while fetching platform OS versions', err)
      })
    );
  }

}
