import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse
} from '@angular/common/http';
import { RequestCache } from './request-cache';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { of } from 'rxjs';
import {UrlConstantsService} from "../services/url.constants.service";
import { NotificationsService, NotificationType } from 'angular2-notifications';

import packageInfo from '../../../../package.json';
import {Router} from "@angular/router";

@Injectable()
export class CacheInterceptor implements HttpInterceptor {
  cacheAbleURLsAndMaxAge = new Map();
  private dismissed: boolean = false ;

  constructor(private cache: RequestCache,
              private router: Router,
              public notificationsService: NotificationsService,
              private URLConstants: UrlConstantsService) {
    this.cacheAbleURLsAndMaxAge.set(this.URLConstants.naturalTextActionsUrl, 8*60*60*1000);
    // this.cacheAbleURLsAndMaxAge.set(this.URLConstants.dryTestStepResultsUrl, 5*60*1000);
    // this.cacheAbleURLsAndMaxAge.set(this.URLConstants.testStepResultsUrl, 5*60*1000);
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const isCacheAble = !!this.cacheAbleURLsAndMaxAge.get(req.url);
    const cachedResponse = isCacheAble ? this.cache.get(req) : undefined;
    return cachedResponse ? of(cachedResponse) : this.sendRequest(req, next, this.cache);
  }

  sendRequest(
    req: HttpRequest<any>,
    next: HttpHandler,
    cache: RequestCache): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          this.appVersionCheck(event);
          this.handleHttpAuthErrors(event);
          cache.put(req, event, this.cacheAbleURLsAndMaxAge.get(req.url));
        }
      })
    );
  }
  appVersionCheck(event){
    if(!this.dismissed && !!event.headers.get("x-app-version") && event.headers.get("x-app-version") != packageInfo.version) {
      const toast = this.notificationsService.create("<div class='version-content'><div class='version-details'><p>There is new version available </p></div><div class='action-content'><button class='btn' id='Refresh'>Refresh</button><button class='btn ml-0'>Dismiss</button></div></div>", "", NotificationType.Alert, true);
      toast.click.subscribe((event) => {
        console.log(event)
        // @ts-ignore
        if(event.target.id == "Refresh") {
          window.location.reload();
        } else{
          this.dismissed = true;
        }
      })
    }
  }

  handleHttpAuthErrors(event) {
    if(event.status == 401){
      this.router.navigate(['/login'], {queryParams: {error:'bad_auth'}});
    }
  }
}
