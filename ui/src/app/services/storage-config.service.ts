import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {StorageConfig} from "../settings/models/storage-config";

@Injectable({
  providedIn: 'root'
})
export class StorageConfigService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public update(storage: StorageConfig): Observable<StorageConfig> {
    return this.http.put<StorageConfig>(this.URLConstants.storageConfigURL, storage.serialize()).pipe(
      map((data) => new StorageConfig().deserialize(data)),
      catchError((err) => throwError(err))
    );
  }

  public find(): Observable<StorageConfig> {
    return this.http.get(this.URLConstants.storageConfigURL).pipe(
      map((data) => new StorageConfig().deserialize(data)),
      catchError(() => throwError('Problem while fetching Storage Configuration'))
    );
  }

}
