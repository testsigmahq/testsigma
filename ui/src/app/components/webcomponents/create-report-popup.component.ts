import {Component, Inject, OnInit} from "@angular/core";
import {BaseComponent} from "app/shared/components/base.component";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";

import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Router} from "@angular/router";
import * as Highcharts from 'highcharts';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ReportsService} from "../../services/reports.service";


@Component({
  selector: 'custom-report-popup',
  template: `
    <div class="container p-25">
        <h1>New Report</h1>
        <form (ngSubmit)="onSubmit()" #heroForm="ngForm">
          <div class="form-group">
            <label for="name">Name</label>
            <input type="text" class="form-control" id="name"
                   required
                   name="name">
          </div>

          <div class="form-group">
            <label for="alterEgo">Description</label>
            <input type="text" class="form-control" id="alterEgo"
                   name="alterEgo">
          </div>

          <div class="form-group">
            <label for="power">Chart Type</label>
            <select
              class="form-control field">
              <option
                *ngFor="let opt of powers">{{opt}}</option>
            </select>
          </div>

          <div class="form-group">
            <label for="power">Report Module</label>
            <select
              class="form-control field">
              <option
                *ngFor="let opt of modules">{{opt}}</option>
            </select>
          </div>

          <div class="form-group">
            <label for="power">Version</label>
            <select
              class="form-control field">
              <option>{{39}}</option>
            </select>
          </div>

          <div class="form-group">
            <label for="power">Selected Columns</label>
            <select
              class="form-control field" [multiple]="true">
              <option
                *ngFor="let opt of columns">{{opt}}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="power">Order By</label>
            <select
              class="form-control field">
              <option
                *ngFor="let opt of columns">{{opt}}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="power">Group By</label>
            <select
              class="form-control field">
              <option
                *ngFor="let opt of columns">{{opt}}</option>
            </select>
          </div>

          <button type="submit" class="theme-btn-primary border-rds-2 ml-44" [disabled]="!heroForm.form.valid">Submit</button>
        </form>

    </div>
  `
})

export class CreateReportPopupComponent extends BaseComponent implements OnInit {
  public Highcharts: typeof Highcharts = Highcharts;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data:any,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    private router: Router,
    public matModal: MatDialog,
    public reportsService: ReportsService,
    private dialogRef: MatDialogRef<CreateReportPopupComponent>){
    super(authGuard, notificationsService, translate)
  }
  public model:any;
  public powers = ['PIE','BAR','TIMESERIES'];
  public modules = ['TestCase','Element'];
  public columns = ['id','name','status'];


  ngOnInit() {

  }

  onSubmit(){
    this.reportsService.create(document.getElementById("name")["value"]).subscribe((res)=>{
      console.log(res);
    });
    this.matModal.closeAll();
  }

}
