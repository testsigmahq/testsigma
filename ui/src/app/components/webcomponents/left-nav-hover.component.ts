import {Component, OnInit, EventEmitter, Input, Output} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-left-nav-hover',
  template: `
    <div
      (mouseover)="resetHoverItem()"
      class="bg-white border-rds-right-4 global-create-modal p-15 shadow-left ml-n12">
      <li
        [class.active]="isCurrentActiveOrHover('dashboard')"
        [routerLink]="['/dashboard']" [routerLinkActive]="'active'">
        <a
          (click)="isClickedLabel()"
          [class.active]="isCurrentActiveOrHover('dashboard')"
          [routerLink]="['/dashboard']" [routerLinkActive]="'active'" style="margin-bottom: 11px">
          <span class="fa-circle-filled pr-10" style=" font-size: 4px!important; color: #03A973;"></span>
          <span [translate]="'left_nav.dashboard'">
        </span>
        </a>
      </li>
      <li
        [class.active]="isCurrentActiveOrHover('td')"
        class="primary-nav-item test-development-popup"
        [routerLink]="['/td']" [routerLinkActive]="'active'">
        <a
          (click)="isClickedLabel()"
          [class.active]="isCurrentActiveOrHover('td')"
          [routerLink]="['/td']" [routerLinkActive]="'active'" class="text-nowrap" style="margin: 0px 0">
          <span class="fa-circle-filled pr-10" style=" font-size: 4px!important; color: #03A973;"></span>
          <span [translate]="'left_nav.test_development'">
        </span>
        </a>
      </li>
      <li [class.active]="isCurrentActiveOrHover('workspaces')"
          [routerLink]="['/workspaces']" [routerLinkActive]="'active'">
        <a
          (click)="isClickedLabel()" [class.active]="isCurrentActiveOrHover('workspaces')"
          [routerLink]="['/workspaces']" [routerLinkActive]="'active'" style="margin: 10px 0">
          <span class="fa-circle-filled pr-10" style=" font-size: 4px!important; color: #03A973;"></span>
          <span [translate]="'left_nav.workspace_settings'">
        </span>
      </a>
    </li>
      <li [class.active]="isCurrentActiveOrHover('agents')"
          [routerLink]="['/agents']" [routerLinkActive]="'active'">
        <a
          (click)="isClickedLabel()" [class.active]="isCurrentActiveOrHover('agents')"
          [routerLink]="['/agents']"
          style="margin: 12px 0"
          [routerLinkActive]="'active'">
          <span class="fa-circle-filled pr-10" style=" font-size: 4px!important; color: #03A973;"></span>
          <span
            [translate]="'left_nav.agents'">
        </span>
        </a>
      </li>
      <li
        [class.active]="isCurrentActiveOrHover('addons')"
        [routerLinkActive]="'active'" [routerLink]="['/addons']">
        <a
          (click)="isClickedLabel()"
          [class.active]="isCurrentActiveOrHover('addons')"
          [routerLinkActive]="'active'" [routerLink]="['/addons']" style="margin-top: 12px">
          <span class="fa-circle-filled pr-10" style=" font-size: 4px!important; color: #03A973;"></span>
          <span [translate]="'td_nav.add_ons'">
        </span>
        </a>
      </li>
      <li
        [class.active]="isCurrentActiveOrHover('settings')"
        [routerLink]="['/settings']" [routerLinkActive]="'active'">
        <a
          (click)="isClickedLabel()"
          [class.active]="isCurrentActiveOrHover('settings')"
          [routerLink]="['/settings']" [routerLinkActive]="'active'" style="margin: 12px 0">
          <span class="fa-circle-filled pr-10" style=" font-size: 4px!important; color: #03A973;"></span>
          <span [translate]="'left_nav.settings'">
        </span>
      </a>
    </li>

    </div>
  `,
  styles: [
  ]
})
export class LeftNavHoverComponent extends BaseComponent implements OnInit {
  @Input('currentHoverItem') currentHoverItem: string;
  @Output('isClicked') isClicked = new EventEmitter<boolean>();
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService) {
    super(authGuard, notificationsService, translate);
  }

  ngOnInit(): void {
  }

  isCurrentActiveOrHover(itemName){
    return this.currentHoverItem == itemName;
  }

  resetHoverItem() {
    this.currentHoverItem = '';
  }
  isClickedLabel(){
    this.isClicked.emit(false)
  }

}
