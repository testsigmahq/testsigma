import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {SchedulePlanService} from "../../services/schedule-plan.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {fromEvent} from 'rxjs/internal/observable/fromEvent';
import {ScheduleType} from "../../enums/schedule-type.enum";
import {DateFormatPipe} from 'ngx-moment';
import {TestPlanService} from "../../services/test-plan.service";
import {TestPlan} from "../../models/test-plan.model";
import {MatDialog} from '@angular/material/dialog';
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import * as moment from 'moment';
import {SchedulePlan} from "../../models/schedule-plan.model";
import {ScheduleStatus} from "../../enums/schedule-status.enum";
import * as momentZone from "moment-timezone";
import { SchedulePlanFormComponent } from '../webcomponents/schedule-plan-form.component';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-schedules',
  templateUrl: './schedules.component.html',
  styles: [],
  host: {
    'class': 'd-flex ts-col-100 h-100'
  }
})
export class SchedulesComponent extends BaseComponent implements OnInit {
  public schedules: InfiniteScrollableDataSource;
  public testPlanId: number;
  public dateFormatPipe = new DateFormatPipe();
  public testPlan: TestPlan;
  @ViewChild('searchInput', {static: true}) searchInput: ElementRef;
  public isFiltered: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testPlanService: TestPlanService,
    private schedulePlanService: SchedulePlanService,
    private route: ActivatedRoute,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testPlanId = this.route.parent.snapshot.params.testPlanId;
    this.fetchTestPlan();
    this.fetchSchedules("")
    this.attachSearchEvents();
  }

  fetchTestPlan() {
    this.testPlanService.find(this.testPlanId).subscribe(res => this.testPlan = res);
  }

  fetchSchedules(term?: string) {
    let query = "testPlanId:" + this.testPlanId;
    this.isFiltered = term? true : false;
    if (term)
      query += ",name:*" + term + "*";
    this.schedules = new InfiniteScrollableDataSource(this.schedulePlanService, query);

  }

  getCurrentTime(time) {
    let currentTime;
    currentTime = momentZone(time);
    currentTime.tz(Intl.DateTimeFormat().resolvedOptions().timeZone);
    return currentTime;
  }

  attachSearchEvents() {
    if (this.searchInput) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            this.fetchSchedules(this.searchInput.nativeElement.value);
          })
        )
        .subscribe();
    } else {
      setTimeout(() => this.attachSearchEvents(), 100);
    }
  }

  getDisplayDay(type, scheduleTime, time): string {
    let displayName = '';
    let month =  moment(scheduleTime).toDate().getMonth();
    let dayValue = moment(scheduleTime).toDate().getDay();
    let dateTime = this.getDateTime(time, month, dayValue);
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

  getDateTime(time?, month?, dayValue?) {

    let date = new Date();
    let timeSeconds = time.split(':');
    let dateTime = new Date(date.getFullYear(),
      month ? month-1 : date.getMonth(), dayValue ? dayValue : date.getDate(), timeSeconds[0],
      timeSeconds[1], timeSeconds[2]);
    return dateTime;
  }

  editSchedule(scheduledPlan) {
    this.matDialog.open(SchedulePlanFormComponent, {
      height: "100vh",
      width: '600px',
      position: {top: '0px', right: '0px'},
      data: {execution: this.testPlan, scheduledPlan: scheduledPlan},
      panelClass: ['mat-dialog', 'rds-none']
    }).afterClosed().subscribe(res=> {
      if(res){
        this.fetchSchedules();
      }
    })
  }

  deleteConfirmation(id) {
    const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
      width: '450px',
      data: {
        description: this.translate.instant('message.common.confirmation.message', {FieldName: 'Schedule Test Plan'})
      },
      panelClass: ['matDialog', 'delete-confirm']
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.destroy(id);
      }
    })
  }

  private destroy(id: any) {
    this.schedulePlanService.destroy(id).subscribe(
      res => {
        this.showNotification(NotificationType.Success, this.translate.instant("message.common.deleted.success", { FieldName: 'Schedule Plan' }) )
        this.fetchSchedules(this.searchInput.nativeElement.value);
      },
      err => {
        this.showNotification(NotificationType.Error, this.translate.instant("message.common.deleted.failure", { FieldName: 'Schedule Plan' }) )
        this.fetchSchedules(this.searchInput.nativeElement.value);
      }
    )
  }

  emptySchedules() {
    return this.schedules.isEmpty && !this.isFiltered;
  }

  getCanShowNextInterval(schedule) {
    return ScheduleType.ONCE == schedule?.scheduleType && schedule?.status == ScheduleStatus.IN_ACTIVE;
  }

  getTime(time) {
    return new Date(time).toLocaleTimeString("en-us").split(' ')[0]
  }
}
