import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {EntityExternalMapping} from "../models/entity-external-mapping.model";
import {TestCaseResult} from "../models/test-case-result.model";
import {TestCase} from "../models/test-case.model";
import {TestSuite} from "../models/test-suite.model";
import {TestPlan} from "../models/test-plan.model";
import {TestPlanResult} from "../models/test-plan-result.model";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {TestDataSet} from "../models/test-data-set.model";
import {EntityType} from "../enums/entity-type.enum";

@Injectable({
  providedIn: 'root'
})
export class EntityExternalMappingService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<EntityExternalMapping>> {
    return this.http.get<Page<TestDataSet>>(this.URLConstants.externalMappingsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<EntityExternalMapping>().deserialize(data, EntityExternalMapping)),
      catchError(() => throwError('Problem while fetching entity external mappings'))
    )
  }

  public checkAllEntitiesAreLinked(entityIds : any[], entityType: EntityType){
    let params = new HttpParams().set("ids[]", entityIds.toString()).set("entityType", entityType);
    return this.http.get<void>(this.URLConstants.externalMappingsUrl + "/xray_check_links", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((exception) => throwError(exception))
    );
  }

  public show(id: Number): Observable<EntityExternalMapping> {
    return this.http.get<EntityExternalMapping>(this.URLConstants.externalMappingsUrl + "/" + id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new EntityExternalMapping().deserialize(data)),
      catchError(() => throwError('Problem while fetching TestCaseResultExternalMapping'))
    )
  }

  public destroy(testCaseResultExternalMapping: EntityExternalMapping): Observable<void> {
    return this.http.delete<void>(this.URLConstants.externalMappingsUrl + "/" + testCaseResultExternalMapping.id, {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while deleting TestCaseResultExternalMappings'))
    )
  }

  public create(testCaseResultExternalMapping: EntityExternalMapping): Observable<EntityExternalMapping> {
    return this.http.post<EntityExternalMapping>(this.URLConstants.externalMappingsUrl, testCaseResultExternalMapping.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new EntityExternalMapping().deserialize(data)),
      catchError((error) => throwError(error))
    )
  }

  public pushToXray(testCaseResultExternalMapping: EntityExternalMapping): Observable<EntityExternalMapping> {
    return this.http.post<EntityExternalMapping>(this.URLConstants.externalMappingsUrl + "/push_to_xray", testCaseResultExternalMapping.serialize(), {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => new EntityExternalMapping().deserialize(data)),
      catchError(() => throwError('Problem while initializing result push to Xray'))
    )
  }

}
