import {Component, Input, OnInit} from '@angular/core';
import {SchedulePlanService} from "../../services/schedule-plan.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import * as momentZone from 'moment-timezone';
import {ScheduleType} from "../../enums/schedule-type.enum";
import {ScheduleStatus} from "../../enums/schedule-status.enum";

@Component({
  selector: 'app-schedules-list',
  templateUrl: './schedules-list.component.html',
  styles: []
})
export class SchedulesListComponent implements OnInit {
  @Input('versionId') versionId: number;
  public schedules: InfiniteScrollableDataSource;

  constructor(private schedulePlanService: SchedulePlanService) {
  }

  ngOnInit(): void {
    this.fetchSchedules();
  }

  fetchSchedules() {
    this.schedules = new InfiniteScrollableDataSource(this.schedulePlanService, "versionId:" + this.versionId);
  }

  getDateTime(time?, month?, dayValue?) {

    let date = new Date();
    let timeSeconds = time.split(':');
    let dateTime = new Date(date.getFullYear(),
      month ? month-1 : date.getMonth(), dayValue ? dayValue : date.getDate(), timeSeconds[0],
      timeSeconds[1], timeSeconds[2]);
    return dateTime;
  }

  getCurrentTime(time) {
    let currentTime;
    currentTime = momentZone(time);
    currentTime.tz(Intl.DateTimeFormat().resolvedOptions().timeZone);
    return currentTime;
  }

  getCanShowNextInterval(schedule) {
    return ScheduleType.ONCE == schedule?.scheduleType && schedule?.status == ScheduleStatus.IN_ACTIVE;
  }

  getTime(time) {
    return new Date(time).toLocaleTimeString("en-us").split(' ')[0]
  }
}
