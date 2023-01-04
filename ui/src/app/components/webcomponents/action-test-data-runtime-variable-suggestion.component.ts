import {Component, ElementRef, Inject, Input, OnInit, Optional, ViewChild} from '@angular/core';
import {TestStep} from "../../models/test-step.model";
import {TestCase} from "../../models/test-case.model";
import {FormControl} from "@angular/forms";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TestStepService} from "../../services/test-step.service";
import {TestCaseService} from "../../services/test-case.service";
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {Pageable} from "../../shared/models/pageable";
import {fromEvent} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap } from 'rxjs/operators';
import {TestDataType} from "../../enums/test-data-type.enum";

@Component({
  selector: 'app-action-test-data-runtime-variable-suggestion',
  templateUrl: './action-test-data-runtime-variable-suggestion.component.html'
})
export class ActionTestDataRuntimeVariableSuggestionComponent implements OnInit {
  @Optional() @Input('isFullScreenMode') isFullScreenMode;
  @Optional() @Input('testDataRuntimeVariable') testDataRuntimeVariable;
  @ViewChild('searchInput') searchInput: ElementRef;

  public testSteps: TestStep[] = [];
  public testCaseMap: {[key: string]: TestCase} = {};
  public testCaseStepsMap: {[key: string]: TestStep[]} = {};
  public searchText: string;
  public isNlpTemplatesFetching: Boolean = true;
  public isNlpTemplatesFetchingFailed: Boolean = false;
  public isStepFetching: Boolean = false;
  public storeTemplates: NaturalTextActions[];

  public selectedVersion:FormControl;
  public currentFocusedIndex: number;
  public filteredSuggestion: TestStep[] = [];
  private isQueryBased: boolean;
  constructor(
    private dialogRef: MatDialogRef<ActionTestDataRuntimeVariableSuggestionComponent, any>,
    @Inject(MAT_DIALOG_DATA) public option: { projectId?: number, version: WorkspaceVersion ,stepRecorderView: boolean, testCase:TestCase, testCaseSteps: TestStep[]},
    private nlpTemplateService: NaturalTextActionsService,
    private testStepService: TestStepService,
    private testCaseService: TestCaseService,
    private mobileRecorderEventService: MobileRecorderEventService
  ) { }

  ngOnInit(): void {
    if(this.testDataRuntimeVariable) {
      this.option = this.testDataRuntimeVariable;
    }
    this.selectedVersion = new FormControl(this.option.version, []);
    this.fetchTemplates();
    this.selectedVersion.valueChanges.subscribe(() => this.fetchTestSteps(this.storeTemplates));
    this.attachDebounceEvent();
  }

  fetchTemplates() {
    this.nlpTemplateService.findAll('action:store')
      .subscribe(
        (data)=> {
          this.storeTemplates = data.content;
          this.fetchTestSteps(this.storeTemplates);
        },
        ()=> this.isNlpTemplatesFetchingFailed = true,
        ()=> this.isNlpTemplatesFetching = false
      );
  }

  fetchTestSteps(data: NaturalTextActions[]) {
    this.isStepFetching = true;
    let targetIds = data.map(i => i.id);
    let currentTestcaseStepIds = this.option.testCaseSteps.map((item)=> item.id);
    let prerequisite = this.option?.testCase?.preRequisite;
    let priorityTestcases = [this.option.testCase.id, ...(prerequisite? [prerequisite] : [])];
    let pageable:Pageable = new Pageable();

    pageable.pageSize = 1000;

    this.testStepService.findAll(`naturalTextActionId@${targetIds.join('#')},workspaceVersionId:${this.selectedVersion?.value?.id}`, 'position', pageable).subscribe((res)=> {
      this.testSteps = [...res.content.filter(item => currentTestcaseStepIds.includes(item.id)), ...res.content.filter(item => !currentTestcaseStepIds.includes(item.id))];
      this.testSteps.forEach(item => {
        this.testDataValue(item);
      })
      this.isStepFetching = false;
      this.filteredSuggestion = this.testSteps;
    }, error => {
      this.isStepFetching = false;
    })

  }

  fetchTestcase(testCaseId: number) {
    if(this.testCaseMap[testCaseId] === undefined) {
      this.testCaseMap[testCaseId] = null;

      this.testCaseService.show(testCaseId)
        .subscribe((data)=> this.testCaseMap[testCaseId] = data);
    }
  }

  getTestCaseURI(teststep: TestStep) {
    return window.location.origin + '/ui/td/cases/' + this.testCaseMap[teststep.testCaseId + '']?.id + '#position:' + teststep.position;
  }

  fetchTestcaseSteps(testCaseId: number, testStepId: number) {
    if(this.testCaseStepsMap[testCaseId] === undefined) {
      let pageable:Pageable = new Pageable();
      pageable.pageSize = 1000;

      this.testCaseStepsMap[testCaseId] = null;
      this.testStepService.findAll(`testCaseId:${testCaseId}`, 'position', pageable).subscribe(res => {
        this.testCaseStepsMap[testCaseId] = res.content;
        this.testCaseStepsMap[testCaseId][0].setStepDisplayNumber(this.testCaseStepsMap[testCaseId]);
      });
    }
  }

  getTestCaseSteps(item){
    let testcases = this.testCaseStepsMap[item?.testCaseId] || this.fetchTestcaseSteps(item?.testCaseId, item?.id )
    this.filter(this.searchText)
    return testcases;
  }

  getTestStepDisplayNumber(testCaseId: number, testStepId: number) {
    return this.testCaseStepsMap[testCaseId]?.find((step)=> step.id == testStepId)?.stepDisplayNumber;
  }

  selectSuggestion(testStep?: TestStep, dataValue? : String) {
    let suggestion = dataValue ? dataValue : testStep?.dataMap?.attribute;//testStep?.dataMap?.attribute;
    console.log(testStep);
    this.testDataRuntimeVariable ? this.mobileRecorderEventService.returnData.next({type: TestDataType.runtime, data: suggestion}) : this.dialogRef.close(suggestion);
  }

  testDataValue(testStep?: TestStep) {
    let dataValue = [];
    if(testStep.action.indexOf("Store current") > -1){
      dataValue = dataValue ? dataValue : [];
      dataValue.push({value:""}); //testStep?.dataMap?.attribute
    }/* else if(testStep?.dataMap?.testData) {
      Object.keys(testStep?.dataMap?.testData).forEach(item => {
        dataValue = dataValue ? dataValue : [];
        dataValue.push(testStep?.dataMap?.testData[item]);
      })
    }*/ else {
      dataValue =[];
    }
    testStep.runTimeDataList = dataValue;
    return testStep.runTimeDataList;
  }


  closeSuggestion() {
    this.testDataRuntimeVariable ? this.mobileRecorderEventService.setEmptyAction() : this.dialogRef.close();
  }

  setVersion(version: WorkspaceVersion) {
    this.clearSearchText();
    this.selectedVersion.patchValue(version);
  }

  attachDebounceEvent() {
    if (this.searchInput && this.searchInput.nativeElement) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(200),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (["ArrowDown", "ArrowUp"].includes(event.key))
              return;
            if (this.searchTextName) {
              this.searchText = this.searchTextName
              this.filter(this.searchText);
            } else {
              this.searchText = '';
              this.isQueryBased=false;
              this.filteredSuggestion = this.testSteps;
            }
          })
        )
        .subscribe();
      setTimeout(()=> {
        this.searchInput.nativeElement.focus();
      }, 300);
    } else
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
  }

  get searchTextName(){
    return this.searchInput.nativeElement.innerHTML.replace(/<br>/g, "");
  }

  clearSearchText(){
    if(this.searchInput?.nativeElement)
      this.searchInput.nativeElement.innerHTML = "";
    else
      this.attachDebounceEvent();
    this.searchText = "";
  }

  filter(searchText?: string) {
    if (searchText && searchText.length) {
      this.filteredSuggestion = [];
      if(this.testSteps?.length){
        this.testSteps.forEach(suggestion => {
          Object.keys(suggestion.dataMap.testData).forEach(item => {
            if(suggestion.dataMap.testData[item].value.includes(searchText)){
              this.filteredSuggestion.push(suggestion);
            }
          })
        })
        this.filteredSuggestion = [...new Set(this.filteredSuggestion)];
      }
    } else if (!searchText) {
      this.filteredSuggestion = this.testSteps;
    }
  }

  /**
   * This method returns true if the window is an steprecorder otherwise false
   */
  get isStepRecorderView(){
    return this.option.stepRecorderView;
  }

}
