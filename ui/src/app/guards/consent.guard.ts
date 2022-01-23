import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateChild, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';
import {SessionService} from "../shared/services/session.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {ServerService} from "../services/server.service";
import {Server} from "../models/server.model";

@Injectable({
  providedIn: 'root'
})
export class ConsentGuard implements CanActivateChild {

  isConsentRequestDone: boolean;
  server: Server;
  constructor(
    private authGuard: AuthenticationGuard,
    private serverService: ServerService,
    private sessionService: SessionService,
    private router: Router) {
  }

  canActivateChild(
    childRoute: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    return this.checkConsentRequestDone(state.url) || state.url.indexOf("consent")!=-1;

  }

  private checkConsentRequestDone(url: string): boolean | Promise<boolean | UrlTree> {

    if(this.server != null){
      if(!this.server.consentRequestDone && url.indexOf("consent")==-1)
        return this.router.navigate(["consent"]);
      return this.server.consentRequestDone;

    }
    return this.serverService.find().toPromise().then((server: Server) => {

      this.server = server;
      this.isConsentRequestDone= server.consentRequestDone;

      if(this.isConsentRequestDone)
      {
        if(url.indexOf("consent") != -1){
          this.router.navigate(["dashboard"])
        }
        return true;
      }
      else {
        if(url.indexOf("consent") != -1){
          return true;
        }
        this.router.navigate(["consent"]);
          return true;
      }
    })
  }
}
