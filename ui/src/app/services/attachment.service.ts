import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Attachment} from "../models/attachment.model";

@Injectable({
  providedIn: 'root'
})
export class AttachmentService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<Attachment>> {
    return this.http.get<Page<Attachment>>(this.URLConstants.attachmentsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<Attachment>().deserialize(data, Attachment)),
      catchError(() => throwError('Problem while fetching Attachments'))
    )
  }

  public show(id: Number): Observable<Attachment> {
    return this.http.get<Attachment>(this.URLConstants.attachmentsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new Attachment().deserialize(data)),
      catchError(() => throwError('Problem while fetching Attachment'))
    )
  }

  public create(formData: FormData) {
    return this.http.post<Attachment[]>(this.URLConstants.attachmentsUrl, formData).pipe(
      map(data => new Attachment().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public remove(id: Number): Observable<Attachment> {
    return this.http.delete<Attachment>(this.URLConstants.attachmentsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new Attachment().deserialize(data)),
      catchError(() => throwError('Problem while posting Attachment'))
    )
  }
}
