import {Injectable} from '@angular/core';
import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {Pageable} from "../models/pageable";
import {Page} from "../models/page";
import {HttpClient} from '@angular/common/http';
import {TestDeviceResult} from "../../models/test-device-result.model";
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {DataSourceService} from "./data-source.service";

@Injectable({
  providedIn: 'root'
})
export class TestDeviceResultService implements DataSourceService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestDeviceResult>> {
    return this.http.get<Page<TestDeviceResult>>(this.URLConstants.testDeviceResultsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestDeviceResult>().deserialize(data, TestDeviceResult)),
      catchError(() => throwError('Problem while fetching Execution Results'))
    )
  }

  public show(id: Number): Observable<TestDeviceResult> {
    return this.http.get<TestDeviceResult>(this.URLConstants.testDeviceResultsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestDeviceResult().deserialize(data)),
      catchError(() => throwError('Problem while fetching Execution Result'))
    )
  }

  public update(environmentResult: TestDeviceResult): Observable<TestDeviceResult> {
    return this.http.put<TestDeviceResult>(this.URLConstants.testDeviceResultsUrl + "/" + environmentResult.id, environmentResult.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestDeviceResult().deserialize(data)),
      catchError(() => throwError('Problem while update Execution Result'))
    )
  }
}
