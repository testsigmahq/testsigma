import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestCaseDataDrivenResult} from "../models/test-case-data-driven-result.model";
import {DataSourceService} from "../shared/services/data-source.service";

@Injectable({
  providedIn: 'root'
})
export class TestCaseDataDrivenResultService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestCaseDataDrivenResult>> {
    return this.http.get<Page<TestCaseDataDrivenResult>>(this.URLConstants.testCaseDataDrivenResultsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestCaseDataDrivenResult>().deserialize(data, TestCaseDataDrivenResult)),
      catchError(() => throwError('Problem while fetching TestCaseDataDrivenResults'))
    )
  }

}
