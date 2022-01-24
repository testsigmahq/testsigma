import { Component, OnInit, Input, ChangeDetectorRef, QueryList, ViewChildren, ViewChild } from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {SchedulesCalendarComponent} from "./schedules-calendar.component";
import {SchedulePlanService} from "../../services/schedule-plan.service";
import {TestPlanService} from "../../services/test-plan.service";
import * as moment from 'moment';
import { FormControl } from '@angular/forms';
import { CdkConnectedOverlay } from '@angular/cdk/overlay';

@Component({
  selector: 'app-weekly-schedule-calendar',
  templateUrl: './weekly-schedule-calendar.component.html',
  styles: [
  ]
})
export class WeeklyScheduleCalendarComponent extends SchedulesCalendarComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @ViewChild('eventDetailsOverlay') overlayDir: CdkConnectedOverlay;
  @ViewChildren('eventPopupRef') eventPopupRef: QueryList<any>;

  constructor(
    public cdr: ChangeDetectorRef,
    public schedulePlanService: SchedulePlanService,
    public testPlanService: TestPlanService
  ) {
    super(cdr, schedulePlanService, testPlanService)
  }

  ngOnInit(): void {
    this.viewDate = moment().toDate();
    // we set the timezone to UTC to avoid issues with DST changes
    // see https://github.com/mattlewis92/angular-calendar/issues/717 for more info
    moment.tz.setDefault('Utc');
    super.fetchSchedules(this.version.id);
    this.calendarViewTypeControl = new FormControl('week', []);
  }

}
