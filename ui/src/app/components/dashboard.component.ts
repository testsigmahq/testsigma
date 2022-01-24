import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {UserPreferenceService} from "../services/user-preference.service";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {WorkspaceVersion} from "../models/workspace-version.model";
import {WorkspaceVersionService} from "../shared/services/workspace-version.service";
import {ActivatedRoute, Router} from '@angular/router';
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from "../shared/components/base.component";
import {OnBoardingSharedService} from "../services/on-boarding-shared.service";
import {MatDialog} from "@angular/material/dialog";
import {TelemetryNotificationComponent} from "./webcomponents/telemetry-notification.component";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styles: []
})
export class DashboardComponent extends BaseComponent implements OnInit {
  public version: WorkspaceVersion;
  @ViewChild('shareFeedBackBtn') public shareFeedBackBtn: ElementRef;
  showTelemetryNotification: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public route: ActivatedRoute,
    private router: Router,
    private versionService: WorkspaceVersionService,
    private userPreferenceService: UserPreferenceService,
    public onBoardingSharedService: OnBoardingSharedService,
    public matModal: MatDialog
  ) {
    super(authGuard);
    this.route.queryParams.subscribe(params => {
      this.showTelemetryNotification = params['showTelemetryNotification'];
    });
  }

  ngOnInit(): void {
    this.fetchVersion();
    if (this.showTelemetryNotification) {
      const dialogRef = this.matModal.open<TelemetryNotificationComponent>(TelemetryNotificationComponent, {
        width: '33%',
        position: {top: '25px', right: '25px'},
        panelClass: ['mat-dialog', 'border-rds-6','border-active-t-5']
      });
      dialogRef.afterClosed().subscribe( () => {
        this.router.navigate(['dashboard']);
      })
    }
    this.pushToParent(this.route, this.route.params);
  }

  fetchVersion() {
    this.userPreferenceService.show().subscribe(res => {
      if (res?.versionId) {
        this.versionService.show(res.versionId).subscribe(res => {
          this.version = res;
        }, err => this.loadDemoVersion());
      } else if (res?.projectId) {
        this.versionService.findAll("projectId:" + res.projectId).subscribe(versions => {
          this.version = versions.content[0];
        }, () => {
          this.loadDemoVersion();
        })
      } else {
        this.loadDemoVersion();
      }
    });
  }

  loadDemoVersion() {
    this.versionService.findAll("isDemo:true").subscribe(versions => {
      this.version = versions.content[0];
    })
  }

  get testsigmaOSEnabled() {
    return this.authGuard?.openSource?.isEnabled;
  }


}
