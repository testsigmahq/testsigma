import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {TestStepScreenshot} from "../models/test-step-screenshot.model";

@Injectable({
  providedIn: 'root'
})
export class TestStepScreenshotService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public update(testStepScreenshot: TestStepScreenshot): Observable<TestStepScreenshot> {
    return this.http.put<TestStepScreenshot>(this.URLConstants.testStepScreenshotsUrl + "/" + testStepScreenshot.id, testStepScreenshot.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestStepScreenshot().deserialize(data)),
      catchError(() => throwError('Problem while updating TestStepScreenshot'))
    )
  }


  public destroy(testStepScreenshot: TestStepScreenshot): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testStepScreenshotsUrl + "/" + testStepScreenshot.id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting TestStepScreenshot'))
    )
  }
}
