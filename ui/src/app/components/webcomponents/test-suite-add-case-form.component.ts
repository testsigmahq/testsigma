import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {FormControl} from '@angular/forms';
import {TestPlanAddSuiteFormComponent} from "./test-plan-add-suite-form.component";
import {debounceTime, distinctUntilChanged} from 'rxjs/operators';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {TestCase} from "../../models/test-case.model";
import {TestCaseService} from "../../services/test-case.service";
import {Page} from "../../shared/models/page";
import {Requirement} from "../../models/requirement.model";
import {TestCaseType} from "../../models/test-case-type.model";
import {TestCasePriority} from "../../models/test-case-priority.model";
import {TestCaseTypesService} from "../../services/test-case-types.service";
import {TestCasePrioritiesService} from "../../services/test-case-priorities.service";
import {RequirementsService} from "../../services/requirements.service";
import {TestCaseTagService} from "../../services/test-case-tag.service";
import {TestCaseTag} from "../../models/test-case-tag.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {FilterQuery} from "../../models/filter-query";
import {FilterOperation} from "../../enums/filter.operation.enum";

@Component({
  selector: 'app-test-suite-add-case-form',
  templateUrl: './test-suite-add-case-form.component.html'
})
export class TestSuiteAddCaseFormComponent implements OnInit {
  public availableTestCases: InfiniteScrollableDataSource;
  public selectedTestCases: TestCase[] = [];
  public checkedAvailableCases: TestCase[] = [];
  public checkedSelectedCases: TestCase[] = [];
  public filterTestCaseNameControl = new FormControl();
  public filterTagIds = [];
  public filterRequirementIds: any;
  public requirements = new Page<Requirement>();
  public filterTestCaseTypes: any;
  public testCaseTypesList = new Page<TestCaseType>();
  public testCasePrioritiesList = new Page<TestCasePriority>();
  public testCaseTagList: TestCaseTag[];
  public filterTestCasePriorities: any;
  private testCaseFilter: string;
  public checkAllAvailable = false;
  public checkAllSelected = false;
  public filterApplied: boolean = false;
  public submitted: boolean = false;
  public versionId: string;
  selectedInputValue: any;
  availableInputValue: any;
  public customFieldsQueryHash: FilterQuery[] = [];
  public initialCasesCount;

  constructor(
    @Inject(MAT_DIALOG_DATA) public options: {
      versionFilter: string,
      applicationFilter: string,
      allTestCasesFilter: string,
      activeTestCases: TestCase[],
    },
    private dialogRef: MatDialogRef<TestPlanAddSuiteFormComponent>,
    private testCaseTagService: TestCaseTagService,
    private testCaseService: TestCaseService,
    private requirementsService: RequirementsService,
    private testCasePrioritiesService: TestCasePrioritiesService,
    private testCaseTypesService: TestCaseTypesService) {
  }

  get excludedTestCasesCount(){
    return this.availableTestCases?.cachedItems.filter(testCase => this.selectedCaseIds.indexOf((<TestCase>testCase).id) != -1)?.length || 0;
  }

  ngOnInit(): void {
    this.selectedTestCases = [...this.options.activeTestCases] || [];
    this.selectedTestCases.forEach(testCase => this.setParentCase(testCase));
    let versionQuery = this.options.versionFilter.split(':');
    this.versionId = versionQuery[1];
    this.testCaseFilter = this.options.allTestCasesFilter;
    this.fetchRequirementNames();
    this.fetchTestCasePriorities();
    this.fetchTestCaseTypes();
    this.fetchTestCaseTags();
    this.filterTestCaseNameControl.valueChanges.pipe(debounceTime(100), distinctUntilChanged())
      .subscribe(() => this.constructQueryString());
    this.constructQueryString();
  }

  private fetchTestCases = () => {
    this.availableTestCases = new InfiniteScrollableDataSource(this.testCaseService, this.testCaseFilter, undefined, 0);
    if(this.initialCasesCount == undefined) {
      setTimeout(() => {
        this.initialCasesCount = this.availableTestCases.totalElements + this.selectedTestCases.length;
      }, 1000);
    }
    this.checkAllAvailable = false;
  }

  private fetchRequirementNames = () =>
    this.requirementsService.findAll(this.options.versionFilter).subscribe(res => this.requirements = res);

  private fetchTestCaseTypes = () =>
    this.testCaseTypesService.findAll(this.options.applicationFilter).subscribe(res => this.testCaseTypesList = res);

  private fetchTestCasePriorities = () =>
    this.testCasePrioritiesService.findAll(this.options.applicationFilter).subscribe(res => this.testCasePrioritiesList = res);

  private fetchTestCaseTags = () => this.testCaseTagService.findAll().subscribe(res => this.testCaseTagList = res);


  get selectedCaseIds () {
    return this.selectedTestCases.map(suite => suite.id);
  }

  selectBulk() {
    const checkedAvailableCases = [...this.checkedAvailableCases];
    this.selectedTestCases = this.selectedTestCases.concat(checkedAvailableCases);
    this.checkedAvailableCases = [];
    this.constructQueryString();
  }

  deSelectBulk() {
    this.selectedTestCases = this.selectedTestCases.filter(suite => this.checkedSelectedCases.indexOf(suite) == -1);
    this.checkedSelectedCases = [];
    this.constructQueryString();
  }

  deSelectCase(testCase) {
    let index = this.checkedSelectedCases.indexOf(testCase);
    if (index > -1)
      this.checkedSelectedCases.splice(index, 1);
    index = this.selectedTestCases.indexOf(testCase);
    if(index > -1)
      this.selectedTestCases.splice(index, 1);
    this.selectedTestCases = [...this.selectedTestCases];

    this.constructQueryString();
  }

  selectCase(testCase) {
    const index = this.checkedAvailableCases.indexOf(testCase);
    if (index > -1) {
      this.checkedAvailableCases.splice(index, 1);
    }
    this.selectPreRequisite(testCase);
    this.selectedTestCases = [...this.selectedTestCases];
    this.constructQueryString();
  }

  testCaseFilterQuery(){
    let filterQuery = this.customFieldsQueryHash.find(filterQuery => filterQuery.key == "caseId");
    if(!filterQuery) {
      filterQuery = new FilterQuery();
      filterQuery.key = 'caseId';
      filterQuery.operation = FilterOperation.NOT_IN;
      this.customFieldsQueryHash.push(filterQuery);
    }
    filterQuery.value = this.selectedCaseIds;
  }

  selectPreRequisite(testCase: TestCase) {
    if (testCase.preRequisiteCase) {
      testCase.preRequisiteCase.parentCase = testCase;
      this.selectPreRequisite(testCase.preRequisiteCase);
    }
    if (this.selectedCaseIds.indexOf(testCase.id) == -1)
      this.selectedTestCases.push(testCase);
    this.setParentCase(testCase);
  }

  setParentCase(testCase: TestCase) {
    this.selectedTestCases = this.selectedTestCases.map(existingSuite => {
      if (existingSuite.id == testCase.preRequisiteCase?.id) {
        existingSuite.parentCase = testCase;
      }
      return existingSuite;
    })
  }

  toggleCheck(suite: TestCase, array: TestCase[]) {
    const index = array.indexOf(suite);
    if (index > -1)
      array.splice(index, 1);
    else
      array.push(suite);
  }

  toggleCheckAll(checkAll , array: TestCase[]) {
    let iterable = (array == this.checkedAvailableCases) ? this.availableTestCases['cachedItems'] : this.selectedTestCases;
    if (checkAll) {
        for (let testCase of iterable) if (testCase instanceof TestCase) {
          if(array.indexOf(testCase) == -1)
            array.push(testCase);
        }
    }
    else {
      for (let i = array.length; i > -1; i--) array.splice(i, 1);
    }
  }

  drop(event: CdkDragDrop<TestCase[]>){
    if (event.previousIndex == event.currentIndex) return ;
    moveItemInArray(this.selectedTestCases, event.previousIndex, event.currentIndex);
  }

  constructQueryString() {
    let queryString = "";
    if (this.filterTestCasePriorities?.length)
      queryString += ",priority@" + this.filterTestCasePriorities.join("#")
    if (this.filterTestCaseTypes?.length)
      queryString += ",type@" + this.filterTestCaseTypes.join("#")
    if (this.filterTagIds?.length)
      queryString += ",tagId@" + this.filterTagIds.join("#");

    if (this.filterRequirementIds?.length)
      queryString += ",requirementId@" + this.filterRequirementIds.join("#")

    this.filterApplied = (queryString.length || this.filterTestCaseNameControl.value?.length || this.customFieldsQueryHash.filter(filter => filter.key!="caseId")?.length) > 0;

    if(this.availableInputValue)
      queryString += ",name:*" + this.availableInputValue + "*";
    queryString = this.options.allTestCasesFilter + queryString;
    this.testCaseFilter = queryString;
    this.testCaseFilterQuery();
    this.constructCustomFieldQuery();
    this.fetchTestCases();
  }

  resetFilter() {
    this.filterApplied = false
    this.testCaseFilter = this.options.allTestCasesFilter;
    this.filterTestCasePriorities = [];
    this.filterTestCaseTypes = [];
    this.filterTagIds = [];
    this.filterRequirementIds = [];
    this.filterTestCaseNameControl.setValue(null)
    this.constructQueryString();
  }

  save() {
    this.submitted= true;
    if(this.selectedTestCases.length==0) return;
    this.dialogRef.close(this.selectedTestCases);
  }

  testCasesNotCreated(): Boolean {
    return !this.availableTestCases?.isFetching && this.availableTestCases?.isEmpty && !this.filterApplied && !this.selectedTestCases?.length && !this.availableInputValue?.trim().length;
  }

  searchAvailableCases(term) {
    this.availableInputValue = term;
    this.constructQueryString();
  }

  searchSelectedCases(term){
    this.selectedInputValue = term;
  }

  constructCustomFieldQuery() {
    this.customFieldsQueryHash.forEach(filterQuery => {
      if(filterQuery.operation == FilterOperation.IN) {
        if ((<string[]>filterQuery.value).length != 0)
        this.testCaseFilter += `,${filterQuery.key}@${(<string[]>filterQuery.value).join('#')}`;
      } else if(filterQuery.operation == FilterOperation.NOT_IN ) {
        if ((<string[]>filterQuery.value).length != 0)
        this.testCaseFilter += `,${filterQuery.key};${(<string[]>filterQuery.value).join('#')}`;
      } else
      this.testCaseFilter+=`,${filterQuery.key}:${filterQuery.value}`;
    })
    this.testCaseFilter = ','+this.testCaseFilter;
  }

  getCustomFieldValue(queryParamName) {
    let filterQuery = this.customFieldsQueryHash?.find(filterQuery => filterQuery.key == queryParamName);
    return filterQuery?.value;
  }

  setCreatedByValues(event: any) {
    this.constructQueryString();
  }

  emptyTestCasePageMessage(){
    return  (this.initialCasesCount == this.selectedTestCases.length) && (this.availableInputValue==undefined || this.availableInputValue=='')?
            'test_suites.form.available_cases_added' : 'message.common.search.not_found';
  }
}

