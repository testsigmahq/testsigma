/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component, OnInit, ViewChild} from '@angular/core';
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

@Component({
  selector: 'app-left-nav',
  templateUrl: './left-nav.component.html',
  styles: [],
  animations: [expand, collapse]
})
export class LeftNavComponent extends BaseComponent implements OnInit {
  openDropdown: any;
  public isUsageDetailsVisible: boolean;
  public isTestManager = false;
  public displayGlobalAdd: boolean;
  public moreAction: boolean;
  public userPreference: UserPreference;
  public isNoAuth : boolean;
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
    private router: Router) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
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

  ngOnChanges() {
    this.checkIfGithubStarIsShown();
  }

  checkIfGithubStarIsShown() {
    let showedGitHubStar = false;
    let autoRefresh = undefined;
      this.userPreferenceService.show().subscribe(res => {
      this.userPreference = res;
      if ((moment(this.userPreference.createdDate) < moment().subtract(15, 'minute')) &&
        !this.userPreference?.showedGitHubStar) {

        let dialogRef= this.matDialog.open(TestsigmaLoveComponent, {
          position: {top: '10vh', right: '28vw'},
          panelClass: ['mat-dialog', 'rds-none'],
        });

        dialogRef.afterOpened().subscribe(()=>{
          showedGitHubStar = true;
          clearInterval(autoRefresh);
          this.userPreference.showedGitHubStar = true;
          this.userPreferenceService.save(this.userPreference).subscribe();
        });

      }else{
        autoRefresh = setInterval(() => {
          if(this.userPreference?.showedGitHubStar || showedGitHubStar)
            clearInterval(autoRefresh);
          else
            this.checkIfGithubStarIsShown()
        },60000);
      }
    })
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
}
