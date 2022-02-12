import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ResultConstant} from "../../enums/result-constant.enum";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseType} from "../../models/test-case-type.model";
import {TestCasePriority} from "../../models/test-case-priority.model";
import {TestCaseTypesService} from "../../services/test-case-types.service";
import {TestCasePrioritiesService} from "../../services/test-case-priorities.service";
import {Page} from "../../shared/models/page";
import {TestPlan} from "../../models/test-plan.model";

@Component({
  selector: 'app-test-case-result-filter',
  templateUrl: './test-case-result-filter.component.html',
  styles: []
})
export class TestCaseResultFilterComponent implements OnInit {
  @Input('testPlan') testPlan: TestPlan;
  @Input('filterResult') filterResult: ResultConstant[];
  @Output('toggleFilterAction') toggleFilterAction = new EventEmitter<Boolean>();
  @Output() filterAction = new EventEmitter<any>();

  public showFilter: Boolean;
  public filterApplied: boolean;
  public filterTestCaseTypes: string[];
  public filterTestCasePriorities: string[];
  public testcasePrioritiesList: Page<TestCasePriority>;
  public testCaseTypesList: Page<TestCaseType>;

  constructor(
    public translate: TranslateService,
    private testCaseTypeService: TestCaseTypesService,
    private testCasePriorityService: TestCasePrioritiesService) {
  }

  get resultConstant() {
    return Object.values(ResultConstant);
  }

  get isFilterChanged(): Boolean {
    return this.filterResult != undefined ||
      this.filterTestCaseTypes != undefined ||
      this.filterTestCasePriorities != undefined;
  }

  ngOnInit() {
    this.fetchTypes();
  }

  ngOnchange() {
    this.fetchTypes();
  }

  fetchTypes() {

    this.testCaseTypeService.findAll("workspaceId:"+this.testPlan?.workspaceVersion?.workspaceId).subscribe(res => {
      this.testCaseTypesList = res;
    })
    this.testCasePriorityService.findAll("workspaceId:"+this.testPlan?.workspaceVersion?.workspaceId).subscribe(res => {
      this.testcasePrioritiesList = res;
    });
  }

  toggleFilter() {
    this.showFilter = !this.showFilter;
    this.toggleFilterAction.emit(this.showFilter);
  }

  filter() {
    this.filterApplied = true;
    this.filterAction.emit({
      applyFilter: true,
      filterResult: this.filterResult,
      filterTestCaseTypes: this.filterTestCaseTypes,
      filterTestCasePriorities: this.filterTestCasePriorities
    });
  }

  reset() {
    this.filterResult = undefined;
    this.filterTestCaseTypes = undefined;
    this.filterTestCasePriorities = undefined;
    this.filterApplied = false;
    this.filterAction.emit({applyFilter: false});
  }

}
