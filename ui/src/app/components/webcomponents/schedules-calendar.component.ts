import {ChangeDetectorRef, Component, Input, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {SchedulePlanService} from "../../services/schedule-plan.service";
import {CalendarEvent, CalendarMonthViewBeforeRenderEvent, CalendarWeekViewBeforeRenderEvent} from 'angular-calendar';
import {RRule} from 'rrule';
import {ViewPeriod} from 'calendar-utils';
import {Page} from "../../shared/models/page";
import {SchedulePlan} from "../../models/schedule-plan.model";
import * as moment from 'moment-timezone';
import {ScheduleType} from "../../enums/schedule-type.enum";
import {FormControl} from '@angular/forms';
import {TestPlanService} from "../../services/test-plan.service";
import {CdkConnectedOverlay} from '@angular/cdk/overlay';

@Component({
  selector: 'app-schedules-calendar',
  templateUrl: './schedules-calendar.component.html',
  styles: []
})
export class SchedulesCalendarComponent implements OnInit {
  @Input('versionId') versionId: number;
  viewDate: Date;
  calendarEvents: CalendarEvent[] = [];
  calendarViewTypeControl: FormControl;
  viewPeriod: ViewPeriod;
  public schedules: Page<SchedulePlan>;
  eventDetails: any;
  trigger: any;
  @ViewChild('eventDetailsOverlay') overlayDir: CdkConnectedOverlay;
  @ViewChildren('eventPopupRef') eventPopupRef: QueryList<any>;

  constructor(
    public cdr: ChangeDetectorRef,
    public schedulePlanService: SchedulePlanService,
    public testPlanService: TestPlanService) {
  }

  get view() {
    return this.calendarViewTypeControl.value;
  }

  ngOnInit(): void {
    this.viewDate = moment().toDate();
    this.fetchSchedules();
    this.calendarViewTypeControl = new FormControl('week', []);
  }

  fetchSchedules(versionId?:number) {
    this.schedulePlanService.findAll("versionId:" + (versionId? versionId : this.versionId)).subscribe(res => {
      this.schedules = res;
      if (this.viewPeriod)
        this.populateCalendarEvents();
    })
  }

  populateCalendarEvents() {
    this.calendarEvents = [];
    this.schedules.content.forEach((schedulePlan: SchedulePlan) => {
      if(schedulePlan.scheduleType != ScheduleType.ONCE) {
        this.recurringRule(schedulePlan).all().forEach((date) => {
          this.calendarEvents.push({
            title: schedulePlan.name,
            color: schedulePlan.eventColor,
            start: moment(date).toDate(),
            id: schedulePlan.testPlanId,
          });
        });
      } else {
        this.calendarEvents.push({
          title: schedulePlan.name,
          color: schedulePlan.eventColor,
          start: moment(schedulePlan.scheduleTime).toDate(),
          id: schedulePlan.testPlanId,
        });
      }
    });
    this.cdr.detectChanges();
  }

  recurringRule(schedulePlan: SchedulePlan): RRule {
    let json = {
      freq: schedulePlan.recurringRule,
      byweekday: schedulePlan.byWeekDay,
      dtstart: moment(this.viewPeriod.start).startOf('day').toDate() < moment(schedulePlan.createdAt).startOf('day').toDate() ? moment(schedulePlan.createdAt).startOf('day').toDate() : moment(this.viewPeriod.start).startOf('day').toDate(),
      until: moment(this.viewPeriod.end).endOf('day').toDate(),
    };
    if (schedulePlan.scheduleType == ScheduleType.MONTHLY) {
      json['bymonthday'] = moment(schedulePlan.scheduleTime).toDate().getDate();
    }
    if (this.view == 'week') {
      json['byminute'] = moment(schedulePlan.scheduleTime).toDate().getMinutes();
      json['byhour'] = moment(schedulePlan.scheduleTime).hour() + 1;
    }
    return new RRule(json);
  }

  updateCalendarEvents(
    viewRender: | CalendarMonthViewBeforeRenderEvent | CalendarWeekViewBeforeRenderEvent): void {
    if (!this.viewPeriod || !moment(this.viewPeriod.start).isSame(viewRender.period.start) || !moment(this.viewPeriod.end).isSame(viewRender.period.end)) {
      this.viewPeriod = viewRender.period;
      if (this.schedules)
        this.populateCalendarEvents();
    }
  }

  showEventDetails(event2: Event | string, data) {
    this.fetchExecution(data.id, event2, data);
  }

  fetchExecution(id, event2, data) {
    this.testPlanService.find(id).subscribe(testPlan => {

      let target = event2.target.parentElement;
      if (event2?.target?.title.includes('evn_title')) {
        target = event2.target;
      }
      this.trigger = null;
      this.eventPopupRef['_results'].forEach(refElm => {
        if (refElm['elementRef'].nativeElement.children[0].title == target.title) {
          this.trigger = refElm;
        }
      })
      this.eventDetails = event2;
      this.schedules.content.forEach(schedule => {
        if (testPlan.id == schedule.testPlanId && data.title == schedule.name) {
          schedule.testPlan = testPlan;
          this.eventDetails['data'] = schedule;
        }
      })
      setTimeout(() => {
        this.overlayDir.overlayRef.backdropClick().subscribe(() => {
          this.overlayDir.overlayRef.detach();
          this.eventDetails = null;
        });
      }, 200);
    })
  }

}
