import { Component, OnInit } from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {UserPreferenceService} from "../../services/user-preference.service";


@Component({
  selector: 'app-dashboard-global-add',
  template: `
    <div
      *ngIf="authGuard.session.user"
      class="bg-white border-rds-4 global-create-modal px-12 py-16 shadow-left ml-n7">
      <div
        class="pb-10 theme-border-b rb-medium primary-green"
        [translate]="'global.add_title'"></div>
      <li>
        <a
          [routerLink]="['/td', versionId, 'cases', 'create']"
          class="mt-12 f-medium">
          <i class="fa-test-cases-alt"></i>
          <span [translate]="'global_add.testcase.title'"></span>
        </a>
      </li>
    </div>
  `,
  styles: [
  ]
})
export class DashboardGlobalAddComponent extends BaseComponent implements OnInit {
  public versionId: Number;


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    private userPreferenceService: UserPreferenceService) {
    super(authGuard, notificationsService, translate);
  }

  ngOnInit(): void {
    this.loadUserPreference()
  }

  loadUserPreference() {
    this.userPreferenceService.show().subscribe(res => {
      this.versionId = res?.versionId;
    });
  }
}
