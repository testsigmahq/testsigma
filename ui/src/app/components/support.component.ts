import {Component, OnInit} from "@angular/core";
import {BaseComponent} from "../shared/components/base.component";
import {WorkspaceVersion} from "../models/workspace-version.model";
import {AuthenticationGuard} from "../shared/guards/authentication.guard";
import {ActivatedRoute} from "@angular/router";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {WorkspaceVersionService} from "../shared/services/workspace-version.service";
import {UserPreferenceService} from "../services/user-preference.service";
import {OnBoardingSharedService} from "../services/on-boarding-shared.service";

@Component({
  selector: 'app-support',
  templateUrl: './support.component.html'
})
export class SupportComponent extends BaseComponent implements OnInit {
  public version: WorkspaceVersion;

  constructor(
    public authGuard: AuthenticationGuard,
    public route: ActivatedRoute,
    private versionService: WorkspaceVersionService,
    private userPreferenceService: UserPreferenceService,
    public onBoardingSharedService: OnBoardingSharedService) {
    super(authGuard);
  }

  ngOnInit(): void {
    this.fetchVersion();
    this.openChat();
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

  openChat(){
    // @ts-ignore
    window.fcWidget.open();
  }


}
