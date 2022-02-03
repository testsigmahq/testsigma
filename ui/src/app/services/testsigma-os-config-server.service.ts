import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Observable, throwError} from "rxjs";
import {UserPreference} from "../models/user-preference.model";
import {catchError, map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class TestsigmaOsConfigServerService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public show(): Observable<any> {
    return this.http.get<any>(this.URLConstants.osServerDetailsURL,
    ).pipe(
      map(data => data),
      catchError(() => throwError('Problem while fetching UserPreference'))
    )
  }
}
