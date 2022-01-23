import {Component, Input, OnInit} from '@angular/core';
import {TestDevelopmentComponent} from "../test-development.component";
import {MatDialog} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";

@Component({
  selector: 'app-td-overlay-menu-button',
  templateUrl: './td-overlay-menu-button.component.html',
  styles: [
  ]
})
export class TdOverlayMenuButtonComponent implements OnInit {
  @Input('versionId') versionId: number;

  constructor(
    private matModal: MatDialog,
    private authGuard: AuthenticationGuard
  ) {
  }

  ngOnInit(): void {
  }

  openSecondaryMenu() {
    let isInIframe = false;
    this.matModal.open(TestDevelopmentComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      width: '220px',
      height: '100vh',
      position: {top: '0px', left: isInIframe ? '0px' : '60px', bottom: '0'},
      panelClass: ['mat-overlay', 'project-select-dropdown'],
      data: {versionId: this.versionId}
    });
  }
}
