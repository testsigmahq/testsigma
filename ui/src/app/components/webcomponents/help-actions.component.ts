import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {UsageDetailsComponent} from "./usage-details.component";
import { MatDialog } from '@angular/material/dialog';
import {collapse, expand} from "../../shared/animations/animations";

@Component({
  selector: 'app-help-actions',
  template: `
    <ul
      [class.short-help-container]="!isNav"
      [class.primary-nav]="isNav">
      <li [class.active]="isUsageDetailsVisible" class="align-items-center d-flex justify-content-center">
        <a [class.active]="isUsageDetailsVisible" [matTooltip]="'left_nav.usage_details' | translate" placement="right"
           (click)="showUsageDetails()">
          <span><i class="fa-usage-badge ml-2"></i></span>
        </a>
      </li>
      <li class="mb-5 mt-8 mx-auto separator text-center"></li>
      <li [routerLink]="['/support']" [routerLinkActive]="'active'" class="align-items-center d-flex justify-content-center">
        <a [routerLink]="['/support']"
           [matTooltip]="'left_nav.help' | translate"
           placement="right">
          <span><i class="fa-help"></i></span>
        </a>
      </li>
      <li id="beamer-trigger" class="align-items-center d-flex justify-content-center mb-5" data-beamer-click="true" (click)="onOpenBeamer()">
        <a [matTooltip]="'left_nav.whats_new' | translate" placement="right">
          <span><i class="fa-gift-solid ml-2"></i></span>
        </a>
      </li>
    </ul>
  `,
  styles: [
  ]
})
export class HelpActionsComponent implements OnInit {
  @Input('authGuard') authGuard: AuthenticationGuard;
  @Input('isNav') isNav: boolean;
  @Output('onOpenChat') onOpenChat = new EventEmitter<any>();
  @Output('onOpenHelpDropDown') onOpenHelpDropDown = new EventEmitter<any>();
  public isUsageDetailsVisible:boolean = false;
  constructor(
    public matDialog: MatDialog) { }

  ngOnInit(): void {
  }

  showUsageDetails() {
    this.isUsageDetailsVisible = true;
    let dialogRef = this.matDialog.open(UsageDetailsComponent, {
      width: '85%',
      height: '90%',
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed().subscribe(res => {
      this.isUsageDetailsVisible = false;
    })
  }

  closeDropdown() {
    this.onOpenHelpDropDown.emit()
  }

  onOpenBeamer() {
    // @ts-ignore
    window.Beamer.show(window.beamer_config)
  }
}
