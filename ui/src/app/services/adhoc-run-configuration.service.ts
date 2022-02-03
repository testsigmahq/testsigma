import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {AdhocRunConfiguration} from "../models/adhoc-run-configuration.model";
import {MobileOsVersion} from "../agents/models/mobile-os-version.model";
import {WorkspaceType} from "../enums/workspace-type.enum";

@Injectable({
  providedIn: 'root'
})
export class AdhocRunConfigurationService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService
  ) {
  }

  public findAll(appType: WorkspaceType, sortBy?: string, pageable?: Pageable | undefined): Observable<AdhocRunConfiguration[]> {
    return this.http.get<AdhocRunConfiguration[]>(this.URLConstants.adhocRunConfigurationsUrl + "/" + appType, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(undefined, sortBy, pageable)
    }).pipe(
      map((data) => {
        return data.map(savedConfig => new AdhocRunConfiguration().deserialize(savedConfig));
      }),
      catchError(() => throwError('Problem while fetching Configuration'))
    )
  }

  public create(configuration: AdhocRunConfiguration): Observable<AdhocRunConfiguration> {
    return this.http.post<AdhocRunConfiguration>(this.URLConstants.adhocRunConfigurationsUrl, configuration.serialize())
      .pipe(
        map(data => new AdhocRunConfiguration().deserialize(data)),
        catchError((exception) => throwError(exception))
      )
  }

  public update(configuration: AdhocRunConfiguration): Observable<AdhocRunConfiguration> {
    return this.http.put<AdhocRunConfiguration>(this.URLConstants.adhocRunConfigurationsUrl + "/" + configuration.id, configuration.serialize())
      .pipe(
        map(data => new AdhocRunConfiguration().deserialize(data)),
        catchError((exception) => throwError(exception))
      )
  }

  public delete(id): Observable<void> {
    return this.http.delete<AdhocRunConfiguration>(this.URLConstants.adhocRunConfigurationsUrl + "/" + id)
      .pipe(
        map(data => console.log(data)),
        catchError((exception) => throwError(exception))
      )
  }
}
