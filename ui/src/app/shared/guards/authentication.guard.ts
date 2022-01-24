import {Injectable, SkipSelf, Optional} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import {Observable} from 'rxjs';
import {SessionService} from "app/shared/services/session.service";
import {Session} from "app/shared/models/session.model";
import {AuthUser} from "../../models/auth-user.model";
import {TestsigmaOSConfig} from "../models/testsigma-os-config.model";
import {TestsigmaOsConfigService} from "../services/testsigma-os-config.service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationGuard implements CanActivate, CanActivateChild {
  public session: Session;
  user: AuthUser;
  public openSource: TestsigmaOSConfig;

  constructor(
    private sessionService: SessionService,
    private openSourceService: TestsigmaOsConfigService,
    private router: Router, @Optional() @SkipSelf() parent?: AuthenticationGuard) {
    if (parent) {
      return parent;
    }
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.user) {
      return true;
    }
    return this.checkIsAuthenticated();
  }

  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.user) {
      return true;
    }
    return this.checkIsAuthenticated();
  }

  private checkIsAuthenticated(): Promise<boolean | UrlTree> {
    return this.sessionService.getSession().toPromise().then((res: Session) => {
      this.session = res;
      this.openSourceService.show().subscribe(res => this.openSource = res)
      if (res.user) {
        this.user = res.user;
        return true;
      } else {
        this.router.navigate(['/login']);
        return false;
      }
    }).catch((err) => {
      console.error(err);
      this.router.navigate(['/login']);
      return false;
    });
  }

}
