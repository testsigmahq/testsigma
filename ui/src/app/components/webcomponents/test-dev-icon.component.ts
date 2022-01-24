import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {UserPreferenceService} from "../../services/user-preference.service";
import {OnBoardingSharedService} from "../../services/on-boarding-shared.service";
import {OnBoarding} from "../../enums/onboarding.enum";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";

@Component({
  selector: 'app-test-dev-icon',
  template: `
    <li
      cdkOverlayOrigin #trigger="cdkOverlayOrigin"
      class="primary-nav-item"
      #testDevelopment
      [routerLink]="['/td']" [routerLinkActive]="'active'">
      <a [routerLink]="['/td']" [routerLinkActive]="'active'"
         [matTooltip]="'left_nav.test_development' | translate" placement="right">
        <span>
            <i class="fa-pencil-on-paper"></i>
        </span>
      </a>
    </li>
    <ng-template
      #developmentContainer="cdkConnectedOverlay"
      cdkConnectedOverlay
      [cdkConnectedOverlayBackdropClass]="'cdk-overlay-transparent-backdrop'"
      [cdkConnectedOverlayHasBackdrop]="true"
      [cdkConnectedOverlayOrigin]="trigger"
      [cdkConnectedOverlayPanelClass]="'onboarding-help-container'"
      [cdkConnectedOverlayOpen]="canShowTestDevelopment">
      <app-test-development-popup (onClose)="removeDevelopmentPreference()"></app-test-development-popup>
    </ng-template>
  `,
  styles: [
  ]
})
export class TestDevIconComponent implements OnInit {
  @ViewChild('testDevelopment', {static: false}) public testDevelopmentEleRef: ElementRef;
  public canShowTestDevelopment: boolean = false;
  @ViewChild('developmentContainer') developmentContainerDir: CdkConnectedOverlay;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public matDialog: MatDialog,
    public userPreferenceService: UserPreferenceService,
    private onBoardingSharedService: OnBoardingSharedService) { }

  ngOnInit(): void {
    this.onBoardingSharedService.getPreferencesEmitter().subscribe((res: OnBoarding) => {
      console.log(res);
      if(res == OnBoarding.MANGE_PROJECT)
        setTimeout(() => {
          if (this.canShowTestDevelopmentPopUp()) {
            this.openTestDevelopmentPopup()
          }
        }, 500)
    });
  }

  canShowTestDevelopmentPopUp() {
    return !this.matDialog.openDialogs.length;
  }

  openTestDevelopmentPopup() {

    this.canShowTestDevelopment = true;
    // setTimeout(() => {
    //   this.developmentContainerDir.overlayRef._outsidePointerEvents.subscribe(res => {
    //     this.developmentContainerDir.overlayRef.detach();
    //     this.canShowTestDevelopment = false;
    //   });
    // }, 200);
    // const popUpRef = this.matDialog.open(TestDevelopmentPopupComponent, {
    //   disableClose: true,
    //   backdropClass: 'cdk-overlay-transparent-backdrop',
    //   width: '350px',
    //   panelClass: ['mat-overlay', 'onboarding-help-container']
    // })
    //
    // const matDialogConfig = new MatDialogConfig();
    // const rect: DOMRect = this.testDevelopmentEleRef.nativeElement.getBoundingClientRect();
    // matDialogConfig.position = {left: `${rect.right+7}px`, top: `${(rect.top? rect.top : 162) - 20}px`}
    // popUpRef.updatePosition(matDialogConfig.position);
    // popUpRef.afterClosed().subscribe(() => {
    //   this.onBoardingSharedService.emitCompleteEvent(OnBoarding.TEST_DEVELOPMENT);
    // });
  }
  removeDevelopmentPreference() {
    this.developmentContainerDir?.overlayRef?.detach();
    this.onBoardingSharedService.emitCompleteEvent(OnBoarding.TEST_DEVELOPMENT);
    this.canShowTestDevelopment = false;
  }

}
