import {Injectable} from '@angular/core';
import {Pageable} from "../shared/models/pageable";
import {Observable, throwError} from "rxjs";
import {Page} from "../shared/models/page";
import {catchError, map, shareReplay} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {KibbutzTestDataFunction} from "../models/kibbutz-test-data-function.model";

@Injectable({
  providedIn: 'root'
})
export class KibbutzTestDataFunctionService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) { }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<KibbutzTestDataFunction>> {
    return this.http.get<Page<KibbutzTestDataFunction>>(this.URLConstants.kibbutzUrl+'/test_data_functions', {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => {
        console.log(new Page<KibbutzTestDataFunction>().deserialize(data, KibbutzTestDataFunction))
        return new Page<KibbutzTestDataFunction>().deserialize(data, KibbutzTestDataFunction);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
      catchError(() => throwError('Problem while fetching Kibbutz TDF'))
    )
  }

  public show(id: number): Observable<KibbutzTestDataFunction> {
    return this.http.get<KibbutzTestDataFunction>(this.URLConstants.kibbutzUrl+'/test_data_functions/'+id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        return new KibbutzTestDataFunction().deserialize(data);
      }),
      catchError(() => throwError('Problem while fetching Kibbutz TDF'))
    )
  }
}
