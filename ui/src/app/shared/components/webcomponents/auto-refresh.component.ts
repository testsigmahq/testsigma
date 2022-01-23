import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-auto-refresh',
  template: `
    <div class="d-flex align-items-center justify-content-end pr-10">
      <div
        class="switch-container bordered">
        <label
          class="switch">
          <input
            [checked]="!isDisabledAutoRefresh"
            type="checkbox"
            id="moreOption"
            (change)="toggleAutoRefresh()"
            name="moreOption"/>
          <span
            class="slider round"></span>
        </label>
        <span
          [translate]="'results.list_view.auto_refresh.page'"
          class="switch-lable"></span>
      </div>
      <mat-form-field
        class="mat-select-custom sm ts-col-30 xsm" appearance="fill">
        <mat-select
          disableOptionCentering panelClass="single"
          [disabled]="isDisabledAutoRefresh"
          (selectionChange)="addAutoRefresh()"
          [(ngModel)]="autoRefreshInterval">
          <mat-option
            *ngFor="let result of intervals"
            [value]="result.value"
            [textContent]="'Every '+result.name">
          </mat-option>
        </mat-select>
      </mat-form-field>
    </div>
  `,
  styles: []
})
export class AutoRefreshComponent implements OnInit {
  public isDisabledAutoRefresh: boolean = false;
  @Input('autoRefreshIntervalTime') autoRefreshIntervalTimeList: number
  @Output('autoRefreshIntervalTime') autoRefreshIntervalTime = new EventEmitter<any>();
  @Output('autoRefreshToggle') autoRefreshToggle = new EventEmitter<any>();
  public autoRefreshInterval: any;
  public intervals = [{'name': '10 sec.', 'value': 10000}, {'name': '20 sec.', 'value': 20000}, {
    'name': '30 sec.',
    'value': 30000
  }, {'name': '50 sec.', 'value': 50000}];

  constructor() {
  }

  ngOnInit(): void {
    this.autoRefreshInterval = this.autoRefreshIntervalTimeList ? this.autoRefreshIntervalTimeList : 10000;
  }

  addAutoRefresh() {
    this.autoRefreshIntervalTime.emit(this.autoRefreshInterval)
  }

  toggleAutoRefresh() {
    this.isDisabledAutoRefresh = !this.isDisabledAutoRefresh;
    this.autoRefreshToggle.emit(this.isDisabledAutoRefresh)
  }
}
