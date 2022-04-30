import {Injectable} from '@angular/core';
import {Pageable} from "../../shared/models/pageable";
import {Observable} from "rxjs/internal/Observable";
import {Page} from "../../shared/models/page";
import {map} from "rxjs/internal/operators/map";
import {catchError} from "rxjs/internal/operators/catchError";
import {throwError} from "rxjs/internal/observable/throwError";
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../../shared/services/http-headers.service";
import {UrlConstantsService} from "../../shared/services/url.constants.service";
import {BackupVersionModel} from "../models/backup.version.model";
import {Backup} from "../models/backup.model";

@Injectable({
  providedIn: 'root'
})
export class BackupService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(pageable?: Pageable): Observable<Page<Backup>> {
    return this.http.get<Page<Backup>>(this.URLConstants.backupUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(undefined, undefined, pageable)
    }).pipe(
      map(data => new Page<Backup>().deserialize(data, Backup)),
      catchError(() => throwError('Problem while fetching Backups'))
    )
  }

  public create(backupModel:BackupVersionModel): Observable<Backup> {
    return this.http.post<Backup>(this.URLConstants.backupExportUrl, backupModel.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new Backup().deserialize(data)),
      catchError((exception) => throwError(exception))
    );
  }

  public importXml(formData:FormData): Observable<BackupVersionModel> {
    return this.http.post<BackupVersionModel>(this.URLConstants.backupUrl, formData, {
    }).pipe(
      map(data => new BackupVersionModel().deserialize(data)),
      catchError((exception) => throwError(exception))
    );
  }

  public delete(id): Observable<void> {
    return this.http.delete<void>(this.URLConstants.backupUrl + "/" + id)
      .pipe(
        map(data => console.log(data)),
        catchError((exception) => throwError(exception))
      )
  }

}
