import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {ResultConstant} from "../../enums/result-constant.enum";
import {FilterTimePeriod} from "../../enums/filter-time-period.enum";
import {ExecutionTriggeredType} from "../../enums/triggered-type.enum";

@Component({
  selector: 'app-run-list-filter',
  templateUrl: './run-list-filter.component.html',
  styles: []
})
export class RunListFilterComponent implements OnInit {
  @Output() filterAction = new EventEmitter<Boolean>();
  public filterResult: ResultConstant[];
  public filterApplied: Boolean;
  public filterStartTime: FilterTimePeriod;
  public startTimeFilterOptions: FilterTimePeriod[];
  public  filterTriggeredType : ExecutionTriggeredType[];

  constructor(@Inject(MAT_DIALOG_DATA) public options: { filterResult: ResultConstant[], filterStartTime: FilterTimePeriod, filterTriggeredType : ExecutionTriggeredType[] }) {
    this.filterApplied = this.options.filterResult != undefined || this.options.filterStartTime != undefined;
    this.filterResult = options.filterResult;
    this.filterStartTime = options.filterStartTime;
    this.filterTriggeredType = options.filterTriggeredType;
    this.startTimeFilterOptions = this.getStartTimeFilterOptions();
  }

  get resultConstant() {
    return Object.values(ResultConstant);
  }

  get executionTriggeredTypes(){
    return Object.values(ExecutionTriggeredType);
  }
  get isFilterChanged(): Boolean {
    return this.filterResult != undefined ||
      this.filterStartTime != undefined ||
      this.filterTriggeredType != undefined;
  }

  private getStartTimeFilterOptions(): FilterTimePeriod[] {
    return Object.values(FilterTimePeriod);
  }

  ngOnInit() {
  }

  reset() {
    this.filterResult = undefined;
    this.filterStartTime = undefined;
    this.filterApplied = false;
    this.filterAction.emit(false);
  }

  filter() {
    this.filterApplied = true;
    this.filterAction.emit(true);
  }

}
