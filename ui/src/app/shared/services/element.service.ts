import {Injectable} from '@angular/core';
import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Element} from "../../models/element.model";
import {Observable} from 'rxjs/internal/Observable';
import {throwError} from 'rxjs/internal/observable/throwError';
import {catchError} from 'rxjs/internal/operators/catchError';
import {map} from 'rxjs/internal/operators/map';
import {Pageable} from "../models/pageable";
import {Page} from "../models/page";
import {FilterableDataSourceService} from "./filterable-data-source.service";

@Injectable({
  providedIn: 'root'
})
export class ElementService implements FilterableDataSourceService {
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public filter(filterId: number, versionId: number, pageable?: Pageable, sortBy?: string): Observable<Page<Element>> {
    return this.http.get<Page<Element>>(this.URLConstants.elementURL + "/filter/" + filterId + "?versionId=" + versionId, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(undefined, sortBy, pageable)
    }).pipe(
      map(data => new Page<Element>().deserialize(data, Element)),
      catchError(() => throwError('Problem while fetching Elements'))
    )
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<Element>> {
    return this.http.get<Page<Element>>(this.URLConstants.elementURL, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<Element>().deserialize(data, Element)),
      catchError(() => throwError('Problem while fetching Elements'))
    )
  }

  public create(element: Element): Observable<Element> {
    return this.http.post<Element>(this.URLConstants.elementURL, element.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Element().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public update(id: number, element: Element, reviewSubmittedFrom?: string): Observable<Element> {
    return this.http.put<Element>(this.URLConstants.elementURL + "/" + id, element.serialize(), {
        headers: this.httpHeaders.contentTypeApplication,
        params: Boolean(reviewSubmittedFrom)?new HttpParams().set("reviewSubmittedFrom", reviewSubmittedFrom):null
      }).pipe(
      map(data => new Element().deserialize(data)),
      catchError((error) => throwError(error))
    );
  }

  public destroy(id: number) {
    return this.http.delete(this.URLConstants.elementURL + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Element().deserialize(data)),
      catchError(() => throwError('Problem while deleting Element::' + id))
    );
  }

  public bulkDestroy(ids: number[], workspaceVersionId: number): Observable<void> {
    let params = new HttpParams().set("ids[]", ids.toString()).set("workspaceVersionId", workspaceVersionId.toString());
    return this.http.delete<void>(this.URLConstants.elementURL + "/bulk", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    });
  }

  public show(id: number): Observable<Element> {
    return this.http.get<Element>(this.URLConstants.elementURL + "/" + id, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Element().deserialize(data)),
      catchError(() => throwError('Problem while getting Element::' + id))
    );
  }


  bulkCreate(elements: Element[]): Observable<Element[]> {
    let request = [];
    elements.forEach(element => request.push(element.serialize()));
    return this.http.post<Element[]>(this.URLConstants.elementURL + "/bulk", request, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => {
        let elements: Element[] = [];
        data.forEach(element => {
          elements.push(new Element().deserialize(element))
        })
        return elements;
      }),
      catchError((error, fieldErrors) => throwError((fieldErrors) ? 'duplicate' : 'Problem while bulk creating Element::' + elements))
    );
  }

  bulkUpdateScreenNameAndTag(ids: number[], screenName: string, tags: string[]) {
    let params = new HttpParams().set("ids[]", ids.toString())
                                 .set("screenName", screenName)
    return this.http.put<Element>(this.URLConstants.elementURL + "/bulk_update" , tags,
      {
        headers: this.httpHeaders.contentTypeApplication,
        params: params
      }).pipe(
        map(data => new Element().deserialize(data)),
        catchError((error) => throwError(error))
      );
  }

  public findEmptyElements(testCaseId: number, versionId: number): Observable<Page<Element>> {
    return this.http.get<Page<Element>>(this.URLConstants.elementURL + "/empty/" + testCaseId , {
      headers: this.httpHeaders.contentTypeApplication,
      params: new HttpParams().set("workspaceVersionId", versionId.toString())
    }).pipe(
      map(data => new Page<Element>().deserialize(data, Element)),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while empty Elements', error);
      })
    )
  }
}
