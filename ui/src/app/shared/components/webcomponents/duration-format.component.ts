import {Component, Input, OnInit} from '@angular/core';
import * as moment from "moment";

@Component({
  selector: 'app-duration-format',
  template: `
    <div [matTooltip]="tooltip" class="bg-transparent">
      <i class="fa-hourglass text-t-secondary"></i>
      <span [textContent]="formattedDuration"></span>
    </div>
  `,
  styles: []
})
export class DurationFormatComponent implements OnInit {
  @Input('duration') duration: number;
  @Input('haveFormatString') haveFormatString: Boolean = false;
  public formattedDuration: String;
  public tooltip: String;

  constructor() {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    let formatData = moment.duration(this.duration);
    let tooltipArray = [];
    let array = [];
    if (formatData.days() > 0) {
      array.push(formatData.days().toString().padStart(2, "0") + (this.haveFormatString ? " D : " : ":"));
      tooltipArray.push(formatData.days() + ' Days, ')
    }
    if (formatData.hours() > 0) {
      array.push(formatData.hours().toString().padStart(2, "0") + (this.haveFormatString ? " H : " : ":"))
      tooltipArray.push(formatData.hours() + ' Hours, ');
    }
    if (formatData.minutes() > 0) {
      array.push(formatData.minutes().toString().padStart(2, "0") + (this.haveFormatString ? " M : " : ":"))
      tooltipArray.push(formatData.minutes() + ' Minutes ');
    } else if (formatData.minutes() == 0) {
      array.push("00" + (this.haveFormatString ? " M : " : ":"));
    }
    if (formatData.seconds() > 0) {
      array.push(formatData.seconds().toString().padStart(2, "0") + (this.haveFormatString ? " S " : ""))
      tooltipArray.push((formatData.minutes() > 0 ? ', ' : '') + formatData.seconds() + ' Seconds');
    } else if (formatData.seconds() == 0) {
      array.push("00" + (this.haveFormatString ? " S " : ":"));
    }
    if (formatData.milliseconds() > 0) {
      array.push((formatData.seconds() > 0 ? '.' : '') + (formatData.milliseconds().toString().substring(0, 2) + (this.haveFormatString ? ": MS" : "")))
      tooltipArray.push((formatData.seconds() > 0 ? ' and ' : '') + formatData.milliseconds() + ' Milliseconds');
    } else if (formatData.seconds() == 0) {
      array.push("000" + (this.haveFormatString ? ": MS" : ""));
    }
    this.formattedDuration = array.join('');
    this.tooltip = tooltipArray.join('');
  }
}
