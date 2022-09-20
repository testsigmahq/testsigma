/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService} from 'angular2-notifications';
import {MatDialog} from '@angular/material/dialog';
import {UserPreferenceService} from "../../services/user-preference.service";
import {CdkConnectedOverlay} from '@angular/cdk/overlay';
import {collapse, expand} from "../../shared/animations/animations";
import {OnBoarding} from "../../enums/onboarding.enum";
import {OnBoardingSharedService} from "../../services/on-boarding-shared.service";
import {SessionService} from "../../shared/services/session.service";
import {Router} from "@angular/router";
import {AuthenticationConfigService} from "../../services/authentication-config.service";
import {AuthenticationType} from "../../shared/enums/authentication-type.enum";
import {DryRunFormComponent} from "./dry-run-form.component";
import {TestsigmaLoveComponent} from "./testsigma-love.component";
import moment from "moment";
import {UserPreference} from "../../models/user-preference.model";
import {TestsigmaGitHubStarLoveComponent} from "../../shared/components/webcomponents/testsigma-github-star-love.component";
import {TestPlanService} from "../../services/test-plan.service";

@Component({
  selector: 'app-left-nav',
  templateUrl: './left-nav.component.html',
  styles: [],
  animations: [expand, collapse]
})
export class LeftNavComponent extends BaseComponent implements OnInit,OnDestroy {
  openDropdown: any;
  public isUsageDetailsVisible: boolean;
  public isTestManager = false;
  public displayGlobalAdd: boolean;
  public moreAction: boolean;
  public userPreference: UserPreference;
  public isNoAuth : boolean;
  public autoRefresh:any;
  @ViewChild('moreActionRef') moreActionOverlay: CdkConnectedOverlay;
  @ViewChild('primaryHelpContainer') overlayDir: CdkConnectedOverlay;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public authConfigService: AuthenticationConfigService,
    public matDialog: MatDialog,
    public userPreferenceService: UserPreferenceService,
    public onBoardingSharedService: OnBoardingSharedService,
    private sessionService: SessionService,
    public testPlanService: TestPlanService,
    private router: Router) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.setIntervalToCheckGithubStar();
    this.checkIfGithubStarIsShown();
    this.onBoardingSharedService.getPreferencesEmitter().subscribe((completedEvent: OnBoarding) => {
      if(completedEvent == OnBoarding.TEST_DEVELOPMENT) {
        setTimeout(()=> {
          this.removePrimaryPreference()
        }, 500);
      }
    })
    this.fetchAuthConfig()
  }
  GithubStarPopup(){
    this.clearIntervalIfGitHubStarIsShown();
    let dialogRef = this.matDialog.open(TestsigmaGitHubStarLoveComponent, {
      position: {top: '10vh', right: '35vw'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        showTwitter: false,
        userPreference: this.userPreference
      }
    });
  }

  checkIfGithubStarIsShown() {
    this.userPreferenceService.show().subscribe(res => {
      this.userPreference = res;
      const testCaseResults = "test_case_results";
      if((moment(this.userPreference.createdDate) < moment().subtract(5, 'minute')) &&
        !this.userPreference?.showedGitHubStar && this.userPreference.clickedSkipForNow<2 && !this.router.url.includes(testCaseResults)) {
        this.GithubStarPopup();
      }

    })
  }

  clearIntervalIfGitHubStarIsShown(){
      clearInterval(this.autoRefresh);
  }

  fetchAuthConfig()
  {
    this.authConfigService.find().subscribe(
      (authConfig) => {
        this.isNoAuth = authConfig.authenticationType == AuthenticationType.NO_AUTH;
      }, error => {
        this.translate.get('message.common.fetch.failure', {FieldName: 'Authentication Config'}).subscribe((res) => {
          this.showAPIError(error, res);
        })
      });
  }

  moreActionOptions() {
    if (this.moreAction == true) return;
    this.moreAction = true;
    setTimeout(() => {
      this.moreActionOverlay.overlayRef._outsidePointerEvents.subscribe(res => {
        this.moreActionOverlay.overlayRef.detach();
        this.moreAction = false;
      });
    }, 350);
  }

  openChat() {
    // @ts-ignore
    window.fcWidget.open();
  }

  onCloseDropdown() {
    this.moreAction = false;
  }

  showBeamer() {
    // @ts-ignore
    window.Beamer.show(window.beamer_config)
  }

  get isMoreOptionShowing() {
    return !(window.getComputedStyle(document?.querySelector('.help-more-icon')).display == "none")
  }

  removePrimaryPreference() {
    this.overlayDir?.overlayRef?.detach();
    this.onBoardingSharedService.emitCompleteEvent(OnBoarding.PRIMARY_HELP);
    // @ts-ignore
    setTimeout(() => this.openChat(), 500);
    let _this = this;
    // @ts-ignore
    window.fcWidget.on("widget:closed", function(resp) {
      _this.onBoardingSharedService.emitCompleteEvent(OnBoarding.CHAT_TALK_TO_US);
    });
  }

  logout() {
    this.sessionService.logout().subscribe(()=> this.router.navigate(['login']));
  }

  private setIntervalToCheckGithubStar() {
    this.autoRefresh = setInterval(() => {
      console.log("autorefresh")
      if (this.userPreference?.showedGitHubStar) {
        console.log("remove")
        this.clearIntervalIfGitHubStarIsShown();
      } else
        this.checkIfGithubStarIsShown()
    }, 60000);
  }

  ngOnDestroy(): void {
    this.clearIntervalIfGitHubStarIsShown();
  }

}
