import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import {SessionService} from "../shared/services/session.service";
import {Session} from "../shared/models/session.model";

@Injectable({
  providedIn: 'root'
})
export class UnAuthenticationGuardGuard implements CanActivate {
  constructor(
    private router: Router,
    private sessionService: SessionService) {
  }
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.checkIsAuthenticated(state);
  }

  private checkIsAuthenticated(state: RouterStateSnapshot): Promise<boolean | UrlTree> | boolean {
    if(state.url.includes('/activate/'))
      return true;
    return this.sessionService.getSession().toPromise().then((res: Session) => {
      if (res.user) {
        console.error("User Session Exists so redirecting to dashboard");
        this.router.navigate(['/dashboard']);
        return false;
      } else {
        return true;
      }
    }).catch((err) => {
      console.error(err);
      return true;
    });
  }

}
