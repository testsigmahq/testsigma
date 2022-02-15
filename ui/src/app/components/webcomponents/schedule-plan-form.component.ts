import {Component, Inject, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestPlan} from "../../models/test-plan.model";
import {SchedulePlan} from "../../models/schedule-plan.model";
import {Page} from 'app/shared/models/page';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DateFormatPipe} from "ngx-moment";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {SchedulePlanService} from "../../services/schedule-plan.service";
import {ScheduleType} from "../../enums/schedule-type.enum";
import * as momentZone from "moment-timezone";
import * as moment from "moment";
import {ScheduleStatus} from "../../enums/schedule-status.enum";

@Component({
  selector: 'app-schedule-plan-form',
  templateUrl: './schedule-plan-form.component.html',
  styles: []
})
export class SchedulePlanFormComponent extends BaseComponent implements OnInit {

  public testPlan: TestPlan;
  public formSubmitted: boolean = false;
  public scheduleList: Page<SchedulePlan>;
  public scheduleForm: FormGroup;
  public scheduledPlan: SchedulePlan;
  public scheduledPlanId: number;
  public dateFormatPipe = new DateFormatPipe();
  public isScheduleTimeInvalid: boolean;
  public today: Date = new Date();
  private dayValue: any;
  private dayName: string;
  private monthValue: string;
  private byWeek: string;
  private prefixes = ['First', 'Second', 'Third', 'Fourth', 'Fifth'];
  public saving: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private matDialog: MatDialog,
    private scheduleService: SchedulePlanService,
    private dialogRef: MatDialogRef<SchedulePlanFormComponent>,
    @Inject(MAT_DIALOG_DATA) public options: {
      execution: TestPlan,
      scheduledPlan: SchedulePlan
    }) {
    super(authGuard, notificationsService, translate, toastrService);
    this.testPlan = this.options.execution;
  }

  ngOnInit(): void {
    this.createSchedule();
    this.addValidations();
    this.formInit();
    if(this.testPlan){
      this.fetchSchedulePlanList(this.testPlan.id);
    }
  }

  createSchedule()
  {
    this.scheduledPlan = this.options?.scheduledPlan || new SchedulePlan();
  }

  addValidations()
  {
    this.scheduleForm = new FormGroup({
      name: new FormControl(this.scheduledPlan.name, [
        Validators.required, Validators.maxLength(125)
      ]),
      description: new FormControl(this.scheduledPlan.description),
      scheduleType: new FormControl(this.scheduledPlan.scheduleType),
      time: new FormControl(),
      date: new FormControl()
    });
  }

  formInit(){
    let currentTime;
    if(this.scheduledPlan?.scheduleTime?.valueOf()){
      currentTime = moment(this.toLocalTime(this.scheduledPlan.scheduleTime));
    } else {
      currentTime = moment(new Date()).local()?.add(10, 'minutes');
    }

    this.scheduleForm.patchValue({
      date: this.dateFormatPipe.transform((this.scheduledPlan?.scheduleTime || moment()), 'YYYY-MM-DD'),
      time: currentTime.format('HH:mm'),
      scheduleType: this.options?.scheduledPlan ? this.options?.scheduledPlan.scheduleType : ScheduleType.ONCE
    })
    this.setDayValue();
  }

  fetchSchedulePlanList(id) {
    this.scheduleService.findAll("testPlanId:" + id+",status:ACTIVE").subscribe(res => {
      this.scheduleList = res;
    })
  }

  create() {
    this.saving = true;
    this.formSubmitted = true;
    this.isScheduleTimeInvalid = false;
    if (this.scheduleForm.valid) {
      if (this.isScheduleValid()) {
        this.setScheduleEntityValues();
        this.scheduleService.create(this.scheduledPlan).subscribe((scheduledPlan) => {
            this.translate.get('message.common.created.success', {FieldName: 'Schedule Plan'}).subscribe((res: string) => {
              this.showNotification(NotificationType.Success, res);
              this.dialogRef.close(scheduledPlan);
              this.saving = false;
            })
          },
          error => {
            this.translate.get('message.common.created.failure', {FieldName: 'Schedule Plan'}).subscribe((res: string) => {
              this.showAPIError(error, res)
              this.saving = false;
            });
          })
      }
      else{
        return false;
      }
    }
  }

  update() {
    this.saving = true;
    this.formSubmitted = true;
    if (this.scheduleForm.valid) {
      if (this.isScheduleValid()) {
        this.setScheduleEntityValues();
        this.scheduleService.update(this.scheduledPlanId, this.scheduledPlan).subscribe(
          (scheduledPlan) => {
            this.translate.get('message.common.update.success', {FieldName: 'Schedule Plan'}).subscribe((res: string) => {
              this.showNotification(NotificationType.Success, res);
              this.dialogRef.close(scheduledPlan);
              this.saving = false;
            });
          },
          error => {
            this.translate.get('message.common.update.failure', {FieldName: 'Schedule Plan'}).subscribe((res: string) => {
              this.showAPIError(error, res)
              this.saving = false;
            });
          });

      }
    }
  }

  getDisplayDay(type): string {
    let displayName = '';
    let dateTime = this.scheduleForm.get('date').value;
    switch (type) {
      case ScheduleType.MONTHLY:
        displayName = moment(dateTime).format("Do");
        break;
      case ScheduleType.WEEKLY:
        displayName = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][new Date(dateTime).getDay()]
        break;
    }

    return displayName;
  }

  get ScheduleType()
  {
    return Object.keys(ScheduleType).filter(type => type != ScheduleType.YEARLY);
  }

  isScheduleValid()
  {
    if (this.getTimeDate() <= new Date()) {
      this.isScheduleTimeInvalid = true;
      return false;
    }
    return true;
  }
  getTimeDate()
  {
    let date = new Date(this.scheduleForm.get('date').value + " " + this.scheduleForm.get('time').value);
    let time = this.scheduleForm.get('time').value.split(":");
    return new Date(date.getFullYear(),
      date.getMonth(), date.getDate(), time[0],
      time[1], 0);
  }

  setScheduleEntityValues() {
    this.scheduledPlan.scheduleTime = this.toUTCTime(this.getTimeDate());
    this.scheduledPlan.testPlanId = this.testPlan.id;
    this.scheduledPlan.scheduleType = this.scheduleForm.get('scheduleType').value;
    this.scheduledPlan.status = ScheduleStatus.ACTIVE;
  }

  toUTCTime(date: Date){
    return moment(date).utc().toDate();
  }
  toLocalTime(date: Date){
    return moment.utc(date).toDate();
  }

  setDayValue() {
    let date = this.scheduleForm.get('date').value;
    this.monthValue = this.dateFormatPipe.transform(date, 'MMMM');
    this.dayValue = this.dateFormatPipe.transform(date, 'DD');
    this.dayName = new Date(date).toLocaleString('en-us', {weekday: 'long'});
    this.byWeek = this.prefixes[Math.floor(new Date(date).getDate() / 7)]
  }

}
