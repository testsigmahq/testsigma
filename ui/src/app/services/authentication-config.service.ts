import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {AuthenticationConfig} from "../models/authentication-config.model";
import {AuthenticationType} from "../shared/enums/authentication-type.enum";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationConfigService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }
  public regenerate(authType: AuthenticationType){
    return this.http.put<void>(this.URLConstants.authConfigURL+"/regenerate/"+authType,undefined);
  }
  public update(authConfig: AuthenticationConfig): Observable<AuthenticationConfig> {
    return this.http.put<AuthenticationConfig>(this.URLConstants.authConfigURL, authConfig.serialize()).pipe(
      map((data) => new AuthenticationConfig().deserialize(data)),
      catchError(() => throwError('Problem while saving Authentication Configuration'))
    );
  }

  public find(): Observable<AuthenticationConfig> {
    return this.http.get(this.URLConstants.authConfigURL).pipe(
      map((data) => new AuthenticationConfig().deserialize(data)),
      catchError(() => throwError('Problem while fetching Authentication Configuration'))
    );
  }
}
