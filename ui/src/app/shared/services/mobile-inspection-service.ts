import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {HttpHeadersService} from './http-headers.service';
import {UrlConstantsService} from './url.constants.service';
import {Pageable} from '../models/pageable';
import {Observable, throwError} from 'rxjs';
import {Page} from '../models/page';
import {catchError, map} from 'rxjs/operators';
import {MobileInspection} from '../../models/mobile-inspection.model';

@Injectable({
  providedIn: 'root'
})

export class MobileInspectionService {
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<MobileInspection>> {
    return this.http.get<Page<MobileInspection>>(this.URLConstants.mobileInspectionsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<MobileInspection>().deserialize(data, MobileInspection)),
      catchError(() => throwError('Problem while fetching Mobile Inspection Results'))
    )
  }
}
