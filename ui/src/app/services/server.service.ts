import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {StorageConfig} from "../settings/models/storage-config";
import {Observable, throwError} from "rxjs";
import {catchError, map} from "rxjs/operators";
import {Server} from "../models/server.model";

@Injectable({
  providedIn: 'root'
})
export class ServerService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public update(server: Server): Observable<StorageConfig> {
    return this.http.put<StorageConfig>(this.URLConstants.serverURL, server.serialize()).pipe(
      map((data) => new StorageConfig().deserialize(data)),
      catchError(() => throwError('Problem while saving Server Configuration'))
    );
  }

  public find(): Observable<Server> {
    return this.http.get(this.URLConstants.serverURL).pipe(
      map((data) => new Server().deserialize(data)),
      catchError(() => throwError('Problem while fetching Server Configuration'))
    );
  }}
