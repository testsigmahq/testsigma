import {Component, Inject, OnInit} from "@angular/core";
import {BaseComponent} from "app/shared/components/base.component";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";

import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Router} from "@angular/router";
import * as Highcharts from 'highcharts';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";


@Component({
  selector: 'custom-report-popup',
  template: `
    <span>{{"REPORT CHART"}}</span>
    <div class="highChart p-25" >
    <highcharts-chart
                      [Highcharts] = "Highcharts"
                      [options] = "data">
    </highcharts-chart>
  </div>
  `
})

export class CustomReportsPopupComponent extends BaseComponent implements OnInit {
  public Highcharts: typeof Highcharts = Highcharts;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data:any,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    private router: Router,
    private dialogRef: MatDialogRef<CustomReportsPopupComponent>){
    super(authGuard, notificationsService, translate)
  }

  ngOnInit() {

  }

}
