import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {UserPreferenceService} from "../services/user-preference.service";
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {BaseComponent} from "../shared/components/base.component";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {Subscription} from 'rxjs';
import {WorkspaceVersionService} from "../shared/services/workspace-version.service";

@Component({
  selector: 'app-td-redirect',
  template: `
    <router-outlet></router-outlet>`,
  styles: []
})
export class TdRedirectComponent implements OnInit, OnDestroy {
  private routerSub: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private workspaceVersionService: WorkspaceVersionService,
    private userPreferenceService: UserPreferenceService) {
    this.routerSub = this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd) {
        if (val.urlAfterRedirects == "/td")
          this.redirectToVersion();
      }
    });
  }

  redirectToVersion() {
    this.userPreferenceService.show().subscribe(res => {
      if (res?.versionId)
        this.router.navigate(['/td', res.versionId]);
      else if(res?.projectId){
        this.workspaceVersionService.findAll("projectId:" + res.projectId).subscribe(versions => {
          this.router.navigate(['/td', versions.content[0].id]);
        }, ()=>{
          console.log('versions loading has some issues in project ::'+res.projectId+' so switching to default project');
          this.redirectToDemoProject();
         })
      }
      else
        this.redirectToDemoProject();
    });
  }

  redirectToDemoProject() {
    this.workspaceVersionService.findAll("isDemo:true").subscribe(versions => {
      this.router.navigate(['/td', versions.content[0].id]);
    })
  }

  ngOnDestroy() {
    this.routerSub.unsubscribe();
  }

  ngOnInit() {
    this.routerSub = this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd) {
        if (val.urlAfterRedirects == "/td")
          this.redirectToVersion();
      }
    });
  }

}
