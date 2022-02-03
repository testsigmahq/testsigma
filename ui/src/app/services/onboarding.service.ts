import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {TestsigmaOSConfig} from "../shared/models/testsigma-os-config.model";
import {catchError, map} from "rxjs/operators";
import {throwError} from "rxjs";
import {Onboarding} from "../models/onboarding.model";
import {Server} from "../models/server.model";

@Injectable({
  providedIn: 'root'
})
export class OnboardingService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  save(onboarding: Onboarding){
    return this.http.post<Onboarding>(this.URLConstants.onboardingURL,onboarding.serialize()).pipe();
  }

  show() {
    return this.http.get<TestsigmaOSConfig>(this.URLConstants.testsigmaOSConfigURL, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  getOnboardingPreference() {
    return this.http.get<Server>(this.URLConstants.onboardingURL, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Server().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  getOTP(onboarding: Onboarding) {
    return this.http.post<void>(this.URLConstants.onboardingURL+"/otp", onboarding, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }

  activate(otp: string) {
    return this.http.get<void>(this.URLConstants.onboardingURL+"/activate/"+otp, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new TestsigmaOSConfig().deserialize(data)),
      catchError((err) => throwError( err))
    );
  }
}
