import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {ResultConstant} from "../../enums/result-constant.enum";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestStepPriority} from "../../enums/test-step-priority.enum";

@Component({
  selector: 'app-test-case-step-result-filter',
  templateUrl: './test-step-result-filter.component.html',
  styles: []
})
export class TestStepResultFilterComponent implements OnInit {
  @Output() filterAction = new EventEmitter<any>();
  public filterStepResult: ResultConstant[];
  public filterApplied: Boolean;
  public filterStepPriority: TestStepPriority[];

  constructor(@Inject(MAT_DIALOG_DATA) public options: { filterResult: ResultConstant[], filterPriority: TestStepPriority[] }) {
    this.filterApplied = this.options.filterResult != undefined;
    this.filterStepResult = options.filterResult;
    this.filterStepPriority = options.filterPriority;
  }

  ngOnInit() {
  }

  reset() {
    this.filterStepResult = undefined;
    this.filterStepPriority = undefined;
    this.filterApplied = false;
    this.filterAction.emit(false);
  }

  filter() {
    this.filterApplied = true;
    this.filterAction.emit(true);
  }

  get resultConstant() {
    return Object.values(ResultConstant);
  }

  get stepPriority() {
    return Object.values(TestStepPriority);
  }

  get isFilterChanged(): Boolean {
    return this.filterStepResult != undefined || this.filterStepPriority != undefined;
  }

}
