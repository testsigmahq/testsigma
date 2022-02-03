import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {StepResultScreenshotComparision} from "../models/step-result-screenshot-comparision.model";

@Injectable({
  providedIn: 'root'
})
export class StepResultScreenshotComparisonService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<StepResultScreenshotComparision>> {
    return this.http.get<Page<StepResultScreenshotComparision>>(this.URLConstants.screenshotComparisonsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<StepResultScreenshotComparision>().deserialize(data, StepResultScreenshotComparision)),
      catchError(() => throwError('Problem while fetching StepResultScreenshotComparisons'))
    )
  }

  public show(id: Number): Observable<StepResultScreenshotComparision> {
    return this.http.get<StepResultScreenshotComparision>(this.URLConstants.screenshotComparisonsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new StepResultScreenshotComparision().deserialize(data)),
      catchError(() => throwError('Problem while fetching StepResultScreenshotComparison'))
    )
  }


  public markAsBase(id: Number): Observable<StepResultScreenshotComparision> {
    return this.http.put<StepResultScreenshotComparision>(this.URLConstants.screenshotComparisonsUrl + "/" + id + "/mark_as_base", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new StepResultScreenshotComparision().deserialize(data)),
      catchError(() => throwError('Problem while MarkingAsBase StepResultScreenshotComparison'))
    )
  }

  public update(screenComparison: StepResultScreenshotComparision): Observable<StepResultScreenshotComparision> {
    return this.http.put<StepResultScreenshotComparision>(this.URLConstants.screenshotComparisonsUrl + "/" + screenComparison.id, screenComparison.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new StepResultScreenshotComparision().deserialize(data)),
      catchError(() => throwError('Problem while MarkingAsBase StepResultScreenshotComparison'))
    )
  }
}
