import {Component} from '@angular/core';
import {AuthenticationGuard} from "./shared/guards/authentication.guard";
import {Title} from '@angular/platform-browser';
import {filter} from 'rxjs/operators';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {UserPreference} from "./models/user-preference.model";
import {UserPreferenceService} from "./services/user-preference.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent{
  title = 'testsigma-angular';
  public userPreference: UserPreference;
  previousUrl: string = null;
  currentUrl: string = null;

  constructor(public authGuard: AuthenticationGuard,
              public titleService: Title,
              public router: Router,
              private activatedRoute: ActivatedRoute,
              public translate: TranslateService,
              public userPreferenceService: UserPreferenceService) {
  }

  get isOnboardingRoute() {
    return this.router.url.indexOf("/onboarding") != -1 || this.router.url.indexOf("/login") != -1 || this.router.url.indexOf("showTelemetryNotification") != -1;
  }

  ngOnInit() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
    )
      .subscribe((event: NavigationEnd) => {
        this.previousUrl = this.currentUrl;
        this.currentUrl = event.url;
        this.getChild(this.activatedRoute).data.subscribe(data => {
          if (data.title) {
            this.translate.get(data.title).subscribe(pageName => {
              this.translate.get("page_title", {pageTitle: pageName}).subscribe(res => {
                let title: string = res;
                this.titleService.setTitle(title);
              });
            });
          }
        });
      });
    this.fetchUserPreference()
  }

  fetchUserPreference() {
    if (this.authGuard?.session?.user?.id) {
      this.userPreferenceService.show().subscribe(res => {
        this.userPreference = res;
      })
    } else {
      setTimeout(() => {
        this.fetchUserPreference()
      }, 200)
    }
  }

  getChild(activatedRoute: ActivatedRoute) {
    if (activatedRoute && activatedRoute.firstChild) {
      return this.getChild(activatedRoute.firstChild);
    } else {
      return activatedRoute;
    }
  }
}
