import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {TestStepResult} from "../models/test-step-result.model";
import {catchError, map} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TestStepResultService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestStepResult>> {
    return this.http.get<Page<TestStepResult>>(this.URLConstants.testStepResultsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestStepResult>().deserialize(data, TestStepResult)),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while fetching TestStepResults', error);
      })
    )
  }

  public show(id: Number): Observable<TestStepResult> {
    return this.http.get<TestStepResult>(this.URLConstants.testStepResultsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestStepResult().deserialize(data)),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while fetching TestStepResult', error);
      })
    )
  }

  public update(testStepResult: TestStepResult): Observable<TestStepResult> {
    return this.http.put<TestStepResult>(this.URLConstants.testStepResultsUrl + "/" + testStepResult.id, testStepResult.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestStepResult().deserialize(data)),
      catchError(() => throwError('Problem while update TestStepResult'))
    )
  }
}
