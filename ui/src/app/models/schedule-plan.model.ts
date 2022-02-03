import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, serializable, SKIP} from "serializr";
import {ScheduleStatus} from "../enums/schedule-status.enum";
import {ScheduleType} from "../enums/schedule-type.enum";
import {TestPlan} from "./test-plan.model";
import {RRule} from 'rrule';

import * as moment from 'moment';

export class SchedulePlan extends Base implements PageObject {

  @serializable
  public id: number;
  @serializable
  public name: string;
  @serializable(alias('comments'))
  public description: String;
  @serializable
  public testPlanId: number;
  @serializable
  public scheduleType: ScheduleType;
  @serializable
  public status: ScheduleStatus;
  @serializable
  public dayOfWeek: number;

  @serializable(alias('scheduleTime',custom(
    v => v ,
    (v) => {
      if(v)
        return moment(v);
    }
  )))
  public scheduleTime: Date;

  public testPlan: TestPlan;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(SchedulePlan, input))
  }

  get recurringRule() {
    switch (this.scheduleType) {
      case ScheduleType.MONTHLY:
        return RRule.MONTHLY;
      case ScheduleType.DAILY:
        return RRule.DAILY;
      case ScheduleType.HOURLY:
        return RRule.HOURLY;
      case ScheduleType.WEEKLY:
        return RRule.WEEKLY;
      case ScheduleType.YEARLY:
        return RRule.YEARLY;
    }
  }

  get byWeekDay() {
    if (this.scheduleType == ScheduleType.WEEKLY) {
      this.dayOfWeek = moment(this.scheduleTime).toDate().getDay() + 1;
      switch (this.dayOfWeek) {
        case 1:
          return [RRule.SU];
        case 2:
          return [RRule.MO];
        case 3:
          return [RRule.TU];
        case 4:
          return [RRule.WE];
        case 5:
          return [RRule.TH];
        case 6:
          return [RRule.FR];
        case 7:
          return [RRule.SA];
      }
    }
  }

  get isWeekly() {
    return this.scheduleType == ScheduleType.WEEKLY;
  }

  get isMonthly() {
    return this.scheduleType == ScheduleType.MONTHLY;
  }

  get eventColor() {
    if (this.isMonthly)
      return { primary: '#ad2121', secondary: '#FAE3E3' };
    else if (this.isWeekly)
      return { primary: '#e3bc08', secondary: '#FDF1BA' };
    else
      return { primary: '#1e90ff', secondary: '#D1E8FF' };
  }
}
