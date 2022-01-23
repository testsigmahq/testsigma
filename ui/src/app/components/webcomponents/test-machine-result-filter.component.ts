import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ResultConstant} from "../../enums/result-constant.enum";

@Component({
  selector: 'app-test-machine-result-filter',
  templateUrl: './test-machine-result-filter.component.html',
  styles: []
})
export class TestMachineResultFilterComponent implements OnInit {
  @Input('executionResult') executionResult: TestPlanResult;
  @Output('toggleFilterAction') toggleFilterAction = new EventEmitter<Boolean>();
  @Output('filterAction') filterAction = new EventEmitter<any>();

  public showFilter: Boolean;
  public filterResult: ResultConstant[];
  public filterApplied: boolean;

  constructor(public translate: TranslateService) {
  }

  ngOnInit() {
  }

  toggleFilter(communicateToParent?: Boolean) {
    this.showFilter = !this.showFilter;
    if (communicateToParent)
      this.toggleFilterAction.emit(communicateToParent);
  }

  filter() {
    this.filterApplied = true;
    this.filterAction.emit({applyFilter: true, filterResult: this.filterResult});
  }

  reset() {
    this.filterResult = undefined;
    this.filterApplied = false;
    this.filterAction.emit({applyFilter: false});
  }

  get resultConstant() {
    return Object.values(ResultConstant);
  }

  get isFilterChanged(): Boolean {
    return this.filterResult != undefined;
  }
}
