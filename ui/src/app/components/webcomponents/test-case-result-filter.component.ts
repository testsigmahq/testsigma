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
import {FormControl, FormGroup} from "@angular/forms";
import {TestCaseTag} from "../../models/test-case-tag.model";

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
  public filterTagIds: number[];
  public tags: TestCaseTag[];
  public createdDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public updatedDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  maxDate = new Date();

  constructor(
    public translate: TranslateService,
    private testCaseTypeService: TestCaseTypesService,
    private testCasePriorityService: TestCasePrioritiesService) {
  }

  dateInvalid(DateRange) {
    return ((DateRange.controls.start.value || DateRange.controls.start.errors?.matDatepickerParse?.text) ||
        (DateRange.controls.end.value || DateRange.controls.end.errors?.matDatepickerParse?.text) ) &&
      DateRange.invalid;
  }

  get resultConstant() {
    return Object.values(ResultConstant);
  }

  get isFilterChanged(): Boolean {
    return this.filterResult != undefined ||
      this.filterTestCaseTypes != undefined ||
      this.filterTestCasePriorities != undefined||
      this.filterTagIds != undefined ||
      (this.updatedDateRange.value.start && !this.dateInvalid(this.updatedDateRange)) ||
      (this.createdDateRange.value.start && !this.dateInvalid(this.createdDateRange));
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
