import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {UserPreference} from "../models/user-preference.model";

@Injectable({
  providedIn: 'root'
})
export class UserPreferenceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public show(): Observable<UserPreference> {
    return this.http.get<UserPreference>(this.URLConstants.userPreferencesUrl, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new UserPreference().deserialize(data)),
      catchError(() => throwError('Problem while fetching UserPreference'))
    )
  }

  save(userPreference: UserPreference): Observable<UserPreference> {
    return this.http.put<UserPreference>(this.URLConstants.userPreferencesUrl, userPreference.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new UserPreference().deserialize(data)),
      catchError(() => throwError('Problem while saving UserPreference'))
    )
  }
}
