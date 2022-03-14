import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Observable, throwError} from "rxjs";
import {Page} from "../shared/models/page";
import {catchError, map} from "rxjs/operators";
import {TestStep} from "../models/test-step.model";
import {TestStepPriority} from "../enums/test-step-priority.enum";
import {RestStepEntity} from "../models/rest-step-entity.model";
@Injectable({
  providedIn: 'root'
})
export class TestStepService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<TestStep>> {
    return this.http.get<Page<TestStep>>(this.URLConstants.testStepsUlr, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<TestStep>().deserialize(data, TestStep)),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while fetching TestStep', error);
      })
    )
  }

  public show(id: Number): Observable<TestStep> {
    return this.http.get<TestStep>(this.URLConstants.testStepsUlr + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestStep().deserialize(data)),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while fetching TestStep', error);
      })
    )
  }

  public update(testStep: TestStep): Observable<TestStep> {
    return this.http.put<TestStep>(this.URLConstants.testStepsUlr + "/" + testStep.id, testStep.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestStep().deserialize(data)),
      catchError((error) => {
        console.log(error);
        return throwError(error)
      }))
  }

  public destroy(id: number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testStepsUlr + '/' + id).pipe(
      catchError((error) => throwError(error))
    );
  }

  public create(step: TestStep): Observable<TestStep> {
    return this.http.post<TestStep>(this.URLConstants.testStepsUlr, step.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new TestStep().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  public bulkUpdate(steps: TestStep[]): Observable<void> {
    let request = [];
    steps.forEach(step => request.push(step.serialize()));
    return this.http.put<void>(this.URLConstants.testStepsUlr + "/bulk_update", request, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      catchError((error) => throwError(error))
    );
  }

  public bulkDestroy(steps: TestStep[]): Observable<void> {
    return this.http.delete<void>(this.URLConstants.testStepsUlr + "/bulk_delete", {
      headers: this.httpHeaders.contentTypeApplication,
      params: steps.map(step => step.id).reduce((p, id) => p.append('ids[]', id.toString()), new HttpParams())
    }).pipe(
      catchError((error) => throwError(error))
    );
  }

  public bulkUpdateProperties(steps: TestStep[], priority?: TestStepPriority, waitTime?: number, disable?: Boolean, ignoreStepResult?: Boolean): Observable<void> {
    let params = steps.map(step => step.id).reduce((p, id) => p.append('ids[]', id.toString()), new HttpParams());
    if (priority)
      params = params.append("priority", priority);
    if (waitTime)
      params = params.append("waitTime", waitTime.toString());
    if (disable != undefined)
      params = params.append("disabled", disable.toString());
    if (ignoreStepResult != undefined)
      params = params.append("ignoreStepResult", ignoreStepResult.toString());
    return this.http.put<void>(this.URLConstants.testStepsUlr + "/bulk_update_properties", {}, {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      catchError((error) => throwError(error))
    );
  }

  bulkCreate(steps: TestStep[]): Observable<void> {
    let output = steps.map(step => step.serialize());
    return this.http.post<void>(this.URLConstants.testStepsUlr + "/bulk_create", output, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      catchError((exception) => throwError(exception))
    );
  }

  public fetchApiResponse(restStepEntity: RestStepEntity): Observable<RestStepEntity> {
    let request = restStepEntity.serialize()
    return this.http.post<RestStepEntity>(this.URLConstants.testStepsUlr + "/fetch_rest_response", restStepEntity.serializeRawValueSendApi(request), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      catchError((error) => throwError(error))
    );
  }

  public localUrlValidation(testCaseId,url): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append("currentUrl", encodeURI(url));
    return this.http.get<JSON>("/testcases/"+testCaseId+"/localurls" , {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while fetching api response'))
    )
  }
}
