import {EventEmitter, Injectable} from '@angular/core';
import {Pageable} from "../shared/models/pageable";
import {Observable, throwError} from "rxjs";
import {Page} from "../shared/models/page";
import {catchError, map} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {SuggestionEngine} from "../models/suggestion-engine.model";

@Injectable({
  providedIn: 'root'
})
export class SuggestionEngineService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(stepResultId: Number, filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<SuggestionEngine>> {
    return this.http.get<Page<SuggestionEngine>>(this.URLConstants.suggestionEngineUrl + "?stepResultId=" + stepResultId, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => new Page<SuggestionEngine>().deserialize(data, SuggestionEngine)),
      catchError((error) => {
        console.log(error);
        return throwError('Problem while fetching TestStep suggestion', error);
      })
    )
  }

  public findSuggestion() {
    return this.http.get(this.URLConstants.suggestionEngineUrl + "/suggestions", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      catchError((error) => {
        console.log(error);
        return throwError('Problem while fetching TestStep', error);
      })
    )
  }
}
