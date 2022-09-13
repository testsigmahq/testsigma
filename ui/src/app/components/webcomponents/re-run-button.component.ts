import { Component, OnInit, Input } from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import { NotificationsService, NotificationType } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import { Router } from '@angular/router';
import {ReRunPopupComponent} from "../../agents/components/webcomponents/re-run-popup.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-re-run-button',
  template: `
    <div class="dropdown section-title d-inline-block mx-10">
      <button class="border-rds-right-1 btn btn-primary" (click)="showRun()">
        <span [translate]="'btn.common.re_run'"></span>
      </button>
    </div>
  `
})
export class ReRunButtonComponent extends BaseComponent implements OnInit {
  @Input('testPlanResult') parentExecutionResult: TestPlanResult;

  public testPlanResult: TestPlanResult;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testPlanResultService: TestPlanResultService,
    private router: Router,
    private matModal: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
  }

  showRun(){
    const dialogRef = this.matModal.open(ReRunPopupComponent, {
      width: '450px',
      height: '250px',
      panelClass: ['re-run-popup', 'rds-none'],
      disableClose: true,
      data: {parentExecutionResult: this.parentExecutionResult}
    });
    dialogRef.afterClosed().subscribe(runType => {
      console.log("Re Run Popup Closed", runType);
    })
  }

}
