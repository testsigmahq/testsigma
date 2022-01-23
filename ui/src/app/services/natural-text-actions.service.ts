import {Injectable} from '@angular/core';
import {HttpHeadersService} from "../shared/services/http-headers.service";
import {UrlConstantsService} from "../shared/services/url.constants.service";
import {Pageable} from "../shared/models/pageable";
import {Page} from "../shared/models/page";
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map, shareReplay} from 'rxjs/operators';
import {NaturalTextActions} from "../models/natural-text-actions.model";
import {NaturaltextActionExample} from "../models/natural-text-action-example.model";

@Injectable({
  providedIn: 'root'
})
export class NaturalTextActionsService {

  private stepTemplateOrder ={
    "WebApplication": [607, 613, 624, 17, 27, 1, 3, 72, 69, 44, 37, 36, 35, 32, 107, 1039, 1023, 1006, 715, 958, 949, 950, 973, 972, 974, 976, 975, 328, 89, 1022, 636, 34, 32, 109, 971, 4, 1044],
    "MobileWeb": [10073, 10071, 10067, 10064, 10060, 10059, 10038, 10040, 10121, 10053, 10047, 10044, 10043, 10011,  10185, 10164, 10153, 10118, 10112, 10010, 10165, 10034, 10042, 10157, 10103, 10026, 10069, 10116],
    "IOSNative":  [30061, 30055, 30052, 30045, 30111, 30091, 30040, 30086, 30035, 30017, 30007, 30026, 30140, 30128, 30120, 30117, 30024, 30129, 30002, 30006, 30093, 30079, 30084, 30016, 30073, 30010, 30001],
    "AndroidNative":  [20061, 20055, 20052, 20045, 20111, 20091, 20040, 20086, 20035, 20007, 20026, 20140, 20128, 20120, 20117, 20024, 20005, 20079, 20093, 20129, 20084, 20016, 20073, 20010, 20001],
  }
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable | undefined): Observable<Page<NaturalTextActions>> {
    return this.http.get<Page<NaturalTextActions>>(this.URLConstants.naturalTextActionsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => {
        let templates = new Page<NaturalTextActions>().deserialize(data, NaturalTextActions);
        return sortBy ? templates : this.setDisplayOrder(templates, filter)
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
      catchError(() => throwError('Problem while fetching Action Templates'))
    )
  }

  private setDisplayOrder(templates: Page<NaturalTextActions>, filterQuery: string) {
    filterQuery = filterQuery.substring(filterQuery.indexOf("workspaceType:"), filterQuery.length);
    filterQuery = filterQuery.split(":")[1];
    let selectedTemplateOrder = this.stepTemplateOrder[filterQuery];
    templates.content.forEach((item: NaturalTextActions) => {
      if(selectedTemplateOrder?.includes(item.id)) {
        item.displayOrder = 0 - (selectedTemplateOrder?.indexOf(item.id) == 0 ? 1 : selectedTemplateOrder?.indexOf(item.id));
      } else {
        item.displayOrder = 1
      }
    })
    return templates;
  }

  public findTemplateDetails(id: number): Observable<NaturaltextActionExample> {
    return this.http.get<Page<NaturaltextActionExample>>(this.URLConstants.naturalTextActionsUrl+ "/"+ id +"/example").pipe(
      map(data => new NaturaltextActionExample().deserialize(data)),
      catchError(() => throwError('Problem while fetching Action Template details'))
    )
  }

}
