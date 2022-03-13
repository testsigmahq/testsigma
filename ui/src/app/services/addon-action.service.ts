import {Injectable} from '@angular/core';
import {Pageable} from "../shared/models/pageable";
import {Observable, throwError} from "rxjs";
import {Page} from "../shared/models/page";
import {catchError, map, shareReplay} from "rxjs/operators";
import {AddonNaturalTextAction} from "../models/addon-natural-text-action.model";
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {TestCase} from "../models/test-case.model";

@Injectable({
  providedIn: 'root'
})
export class AddonActionService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<AddonNaturalTextAction>> {
    return this.http.get<Page<AddonNaturalTextAction>>(this.URLConstants.kibbutzUrl+'/actions', {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => {
        console.log(new Page<AddonNaturalTextAction>().deserialize(data, AddonNaturalTextAction))
        return new Page<AddonNaturalTextAction>().deserialize(data, AddonNaturalTextAction);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
      catchError(() => throwError('Problem while fetching Addon Action'))
    )
  }

  registerListenerAllWindowMessage(){
    window.addEventListener('message', (event) => {
      console.log(event);
      console.log('Addon listener:', event.data.plugin);
      if(event.data.type == 'SOURCE_UPLOADED' || event.data.type == 'INSTALLED')
        this.installPlugin(event.data.plugin).subscribe(res => console.log(res), error => console.error(error));
      else if(event.data.type == 'UNINSTALLED')
        this.unInstallPlugin(event.data.plugin).subscribe(res => console.log(res), error => console.error(error));
    });
  }

  installPlugin(plugin: JSON) {
    return this.http.post<void>(this.URLConstants.addonUrl, plugin, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => console.log(data)),
      catchError((error) => throwError(error))
    )
  }

  unInstallPlugin(plugin: JSON) {
    return this.http.delete<void>(this.URLConstants.addonUrl+plugin['externalUniqueId'], {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => console.log(data)),
      catchError((error) => throwError(error))
    )
  }
}
