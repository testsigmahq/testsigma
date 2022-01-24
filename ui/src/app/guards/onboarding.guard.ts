import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { Observable } from 'rxjs';
import {Server} from "../models/server.model";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {ServerService} from "../services/server.service";
import {SessionService} from "../shared/services/session.service";

@Injectable({
  providedIn: 'root'
})
export class OnboardingGuard implements CanActivateChild, CanActivate {

  server: Server;
  constructor(
    private authGuard: AuthenticationGuard,
    private serverService: ServerService,
    private sessionService: SessionService,
    private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

      return this.checkOnboarding(state.url) || state.url.indexOf("onboarding")!=-1;
  }


  canActivateChild(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    return this.checkOnboarding(state.url) || state.url.indexOf("onboarding")!=-1;
  }

  checkOnboarding(url: string){
    if(this.server != null){
      if(this.server.onboarded){
        return true;
      }
      else{
        return url.indexOf("onboarding") != -1;
      }
    }
    return this.serverService.find().toPromise().then((server: Server) =>
      {
        this.server = server;
        if(this.server.onboarded){
          if(url.indexOf("onboarding")!=-1)
            this.router.navigate(['dashboard']);
          return true;
        }
        else{
          if(url.indexOf("onboarding")!=-1)
            return true;
          this.router.navigate(["onboarding"]);
          return false
        }
      }
    );

  }
}
