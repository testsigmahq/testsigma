import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {MatDialogRef} from "@angular/material/dialog";
import {OnBoardingSharedService} from "../../services/on-boarding-shared.service";
import {Component, OnInit} from "@angular/core";
import {BaseComponent} from "../../shared/components/base.component";
import {OnBoarding} from "../../enums/onboarding.enum";

@Component({
  selector: 'app-first-chrome-install',
  template: `
    <div class="d-flex">
      <div class="ts-col-30 bg-white-smoke">
        <div  class="chrome-with-logo w-75 h-100 mx-auto"></div>
      </div>
      <div class="ts-col-70 onboading-help-content">
        <div
          class="help-header d-flex">
          <span [translate]="'chrome.install'"></span>
          <button
            (click)="removePreference()" class="close ml-auto" mat-dialog-close></button>
        </div>
        <div
          class="help-body">
          <span [translate]="'first_chrome_install.description'"></span>
          <a
            mat-dialog-close
            (click)="removePreference()"
            class="text-link text-nowrap pl-4"
            target="_blank"
            href="https://testsigma.com/docs/faqs/why-chrome-extension/"
            [translate]="'message.common.lean_more'">
          </a>
        </div>
        <div class="d-flex align-items-center justify-content-end">
          <div
            mat-dialog-close
            (click)="removePreference()"
            class="mr-14 text-t-secondary pointer"
            [translate]="'ui_identifiers.form.chrome_extension.refresh_ifInstalled'"></div>
          <a
            mat-dialog-close
            (click)="removePreference()"
            class="btn btn-purple-blue"
            target="_blank"
            href="https://chrome.google.com/webstore/detail/testsigma/epmomlhdjfgdobefcpocockpjihaabdp?hl=en-US"
            [translate]="'btn.common.install'"></a>
        </div>
      </div>
    </div>
  `,
  styles: [
  ]
})
export class FirstChromeInstallComponent extends BaseComponent implements OnInit {

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    // public helpPreferenceService: HelpPreferenceService,
    private matDialog: MatDialogRef<FirstChromeInstallComponent>,
    private onBoardingSharedService: OnBoardingSharedService
  ) {
    super(authGuard,notificationsService,translate)
  }

  ngOnInit(): void {
    this.matDialog.afterClosed().subscribe(() => {
      this.removePreference()
    })
  }

  removePreference() {
    console.log("Deleted Preference")
    this.onBoardingSharedService.emitCompleteEvent(OnBoarding.FIRST_INSTALL_CHROME);
    // super(OnBoarding.FIRST_INSTALL_CHROME)
  }

}
