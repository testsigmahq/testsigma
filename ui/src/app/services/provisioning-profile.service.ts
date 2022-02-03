import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {ProvisioningProfile} from '../models/provisioning-profile.model';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class ProvisioningProfileService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService
  ) { }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<ProvisioningProfile>> {
    return this.http.get<Page<ProvisioningProfile>>(this.URLConstants.iosSettingUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<ProvisioningProfile>().deserialize(data, ProvisioningProfile)),
      catchError(() => throwError('Problem while fetching Provisioning profile'))
    )
  }

  public show(id: number): Observable<ProvisioningProfile> {
    return this.http.get<ProvisioningProfile>(this.URLConstants.iosSettingUrl + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new ProvisioningProfile().deserialize(data)),
      catchError(() => throwError('Problem while getting Provisioning profile::' + id))
    );
  }

  public update(provisioningProfile: ProvisioningProfile, formData: FormData): Observable<ProvisioningProfile> {
    return this.http.put<ProvisioningProfile>(this.URLConstants.iosSettingUrl + "/" + provisioningProfile.id, formData, {
    }).pipe(
      map(data => new ProvisioningProfile().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public delete(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.iosSettingUrl + '/' + id,
      {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting Provisioning profile'))
    )
  }

  public create(provisioningProfile: ProvisioningProfile): Observable<ProvisioningProfile> {
    return this.http.post<ProvisioningProfile>(this.URLConstants.iosSettingUrl, provisioningProfile.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new ProvisioningProfile().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

}
