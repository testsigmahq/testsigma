import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TestDevice} from "../../models/test-device.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlan} from "../../models/test-plan.model";
import {TestSuiteTagService} from "../../services/test-suite-tag.service";
import {TestSuiteTag} from "../../models/test-suite-tag.model";
import {TestSuiteService} from "../../services/test-suite.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestSuite} from "../../models/test-suite.model";
import {TestStep} from "../../models/test-step.model";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {FormControl} from '@angular/forms';
import {debounceTime} from 'rxjs/operators';
import {MatSelectChange} from '@angular/material/select';
import {TestCase} from "../../models/test-case.model";

@Component({
  selector: 'app-test-plan-add-suite-form',
  templateUrl: './test-plan-add-suite-form.component.html',
  styles: []
})
export class TestPlanAddSuiteFormComponent implements OnInit {
  public tags: TestSuiteTag[];
  public availableSuites: InfiniteScrollableDataSource;
  public selectedSuites: TestSuite[] = [];
  public checkedAvailableSuites: TestSuite[] = [];
  public checkedSelectedSuites: TestSuite[] = [];
  public filterSuiteNameControl = new FormControl();
  public filterTagIds = [];
  public submitted: boolean;
  public checkAllAvailable = false;
  public checkAllSelected = false;
  public isSearchEnable: boolean = false;
  public isNameFilter: boolean = false;
  public isAvailableSuiteEmpty: boolean =false;
  public filterCheckAllSelectedSuites: TestSuite[] = [new TestSuite()]
  public isSearched = false;
  public filteredSuites = [];
  public isE2ESelectionOpted = false;
  public selectedVersion: WorkspaceVersion;

  constructor(
    @Inject(MAT_DIALOG_DATA) public options: {
      testDevice: TestDevice,
      version: WorkspaceVersion,
      execution: TestPlan,
      isE2E: boolean
    },
    public dialogRef: MatDialogRef<TestPlanAddSuiteFormComponent>,
    private testSuiteTagService: TestSuiteTagService,
    private testSuiteService: TestSuiteService) {
  }

  get selectedSuiteIds() {
    return this.selectedSuites.map(suite => suite.id);
  }

  get excludedSuitesCount(){
    return this.availableSuites?.cachedItems.filter(testCase => this.selectedSuiteIds.indexOf((<TestCase>testCase).id) != -1)?.length || 0;
  }

  get filterAvailableSuites() {
    this.filteredSuites = this.availableSuites.cachedItems.filter((suite: TestSuite) => this.selectedSuiteIds.indexOf(suite.id) == -1);
    return this.filteredSuites;
  }

  ngOnInit(): void {
    this.selectedSuites = [...this.options.testDevice?.testSuites] || [];
    this.selectedSuites.forEach(testSuite => this.setParentSuite(testSuite));
    this.filterCheckAllSelectedSuites = [...this.selectedSuites];
    this.fetchTags();
    this.fetchSuites();
    this.filterSuiteNameControl.valueChanges.pipe(debounceTime(1000))
      .subscribe(() => this.fetchSuites(this.filterSuiteNameControl.value));
  }

  fetchTags() {
    this.testSuiteTagService.findAll().subscribe(res => this.tags = res);
  }

  fetchSuites(term?: string) {
    this.checkAllAvailable = false;
    this.isNameFilter = false;
    let query = "workspaceVersionId:" + this.currentApplicationVersion?.id;
    if (term) {
      query += ",name:*" + term + "*";
      this.isNameFilter = true
    }
    if (this.filterTagIds.length > 0)
      query += ",tagId@" + this.filterTagIds.join("#");
    if(this.selectedSuites?.length > 0)
      query += ",id;"+this.selectedSuites.map(suite => suite.id).join("#");
    this.availableSuites = new InfiniteScrollableDataSource(this.testSuiteService, query, "name", 500);
  }

  selectBulk() {
    const checkedAvailableSuites = [...this.checkedAvailableSuites];
    for (let Suites of checkedAvailableSuites) this.selectSuite(Suites);
    this.checkedAvailableSuites = [];
    this.checkEmptyAvailable()
  }

  checkEmptyAvailable() {
    this.isAvailableSuiteEmpty = true;
    if(this.availableSuites?.cachedItems?.length)
    this.availableSuites.cachedItems.map((item: TestSuite) => {
      if(!this.selectedSuiteIds.includes(item.id))
        this.isAvailableSuiteEmpty = false
    })
  }

  deSelectBulk() {
    this.selectedSuites = this.selectedSuites.filter(suite => this.checkedSelectedSuites.indexOf(suite) == -1);
    this.checkedSelectedSuites = [];
    this.filterCheckAllSelectedSuites = [...this.selectedSuites];
    this.fetchSuites();
    this.checkEmptyAvailable()
  }

  selectSuite(suite) {
    const index = this.checkedAvailableSuites.indexOf(suite);
    if (index > -1)
      this.checkedAvailableSuites.splice(index, 1);
    this.selectPreRequisite(suite);
    this.selectedSuites = [...this.selectedSuites];
    this.filterCheckAllSelectedSuites = [...this.selectedSuites];
    this.checkEmptyAvailable()
  }

  selectPreRequisite(suite) {
    if (suite.preRequisiteSuite) {
      suite.preRequisiteSuite.parentSuite = suite;
      this.selectPreRequisite(suite.preRequisiteSuite);
    }
    if (this.selectedSuiteIds.indexOf(suite.id) == -1)
      this.selectedSuites.push(suite);
    this.setParentSuite(suite);
  }

  setParentSuite(suite: TestSuite) {
    this.selectedSuites = this.selectedSuites.map(existingSuite => {
      if (existingSuite.id == suite.preRequisiteSuite?.id) {
        existingSuite.parentSuite = suite;
      }
      return existingSuite;
    })
    this.filterCheckAllSelectedSuites = [...this.selectedSuites];
  }

  deSelectSuite(suite) {
    let index = this.checkedSelectedSuites.indexOf(suite);
    if (index > -1)
      this.checkedSelectedSuites.splice(index, 1);
    index = this.selectedSuites.indexOf(suite);
    this.selectedSuites.splice(index, 1);
    this.selectedSuites = [...this.selectedSuites];
    this.filterCheckAllSelectedSuites = [...this.selectedSuites];
    this.fetchSuites();
    this.checkEmptyAvailable();
  }

  toggleCheck(suite: TestSuite, array: TestSuite[]) {
    const index = array.indexOf(suite);
    if (index > -1)
      array.splice(index, 1);
    else
      array.push(suite);
  }

  drop(event: CdkDragDrop<TestStep[]>) {
    if (event.previousIndex != event.currentIndex)
      moveItemInArray(this.selectedSuites, event.previousIndex, event.currentIndex);
  }

  filterSelected($event: MatSelectChange) {
    this.filterTagIds = $event.value;
    this.fetchSuites(this.filterSuiteNameControl.value);
  }

  save() {
    this.submitted = true;
    if (this.selectedSuites.length == 0) return;
    this.options.testDevice.testSuites = this.selectedSuites;
    this.dialogRef.close(true);
  }

  toggleCheckAll(checkAll, array: TestSuite[]) {
    let testSuites: TestSuite[] = []
    this.availableSuites.cachedItems.map((item: TestSuite) => {testSuites.push(item)})
    let iterable = (array == this.checkedAvailableSuites) ? testSuites : this.selectedSuites;
    if (checkAll)
      for (let testCase of iterable) array.push(testCase);
    else {
      for (let i = array.length; i > -1; i--) array.splice(i, 1);
    }
    if((array == this.selectedSuites))
    this.filterCheckAllSelectedSuites = [...this.selectedSuites];
  }

  searchSelectSuite(name: any) {
    if(name) {
      this.filterCheckAllSelectedSuites = [...this.selectedSuites.filter(suites => suites.name.toLowerCase().includes(name?.toLowerCase()))];
      this.isSearched = true;
    } else {
      this.filterCheckAllSelectedSuites = [...this.selectedSuites];
      this.isSearched = false;
    }
    this.checkedSelectedSuites =[];
    this.checkAllSelected = false;
  }

  testSuitesNotCreated() {
    return !this.availableSuites?.isFetching && this.availableSuites?.isEmpty && !this.isNameFilter && this.filterTagIds.length == 0 ;
  }

  zeroSuites():boolean{
    return this.filteredSuites?.length == 0;
  }

  get hasMixedAppVersion() {
    return [...new Set(this.selectedSuites.map(item=> item.workspaceVersionId))].length > 1;
  }

  get e2eEnabled() {
    return this.isE2ESelectionOpted || this.hasMixedAppVersion;
  }

  handleE2ESwitch(event) {
    this.isE2ESelectionOpted = event.checked;
  }
  setCurrentVersion(version: WorkspaceVersion) {
    this.selectedVersion = version;

    this.fetchTags();
    this.fetchSuites();
  }

  get currentApplicationVersion() {
    return this.selectedVersion || this.options?.version;
  }
}
