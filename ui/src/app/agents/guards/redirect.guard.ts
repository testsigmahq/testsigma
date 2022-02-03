import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';

@Injectable()
export class RedirectGuard implements CanActivate {
  constructor(
    private router: Router) {
  }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (state.url === '/td/elements/record') {
      this.router.navigate(['agents', 'record']);
      return false;
    }
    return true;
  }
}
