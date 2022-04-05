/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, EventEmitter, Inject, OnInit, Output, QueryList, ViewChildren} from "@angular/core";
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestCasePriority} from "../../models/test-case-priority.model";
import {TestCaseType} from "../../models/test-case-type.model";
import {Page} from "../../shared/models/page";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseTypesService} from "../../services/test-case-types.service";
import {TestCasePrioritiesService} from "../../services/test-case-priorities.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestCaseFilter} from "../../models/test-case-filter.model";
import {TestCaseStatus} from "../../enums/test-case-status.enum";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestCaseTag} from "../../models/test-case-tag.model";
import {TestCaseTagService} from "../../services/test-case-tag.service";
import {FilterQuery} from "../../models/filter-query";
import {FilterOperation} from "../../enums/filter.operation.enum";
import {FormControl, FormGroup} from "@angular/forms";
import * as moment from "moment";


@Component({
  selector: 'app-filter',
  templateUrl: './test-cases-filter.component.html',
  styles: []
})
export class TestCasesFilterComponent implements OnInit {

  public showFilter: Boolean;
  public isStepGroup: boolean;
  public filterApplied: boolean;
  public filterTestCaseTypes: number[];
  public filterTestCasePriorities: number[];
  public filterStatuses: TestCaseStatus[];
  public filterByResult: ResultConstant[];
  public filterIsMappedToSuite: string;
  public filterStepGroup: boolean;
  public filterWorkspaceVersionId: number;
  public filterDeleted: boolean;
  public filterTagIds: number[];
  public testCasePrioritiesList: Page<TestCasePriority>;
  public testCaseTypesList: Page<TestCaseType>;
  public tags: TestCaseTag[];
  public customFieldsQueryHash: FilterQuery[] = [];
  public today: Date = new Date();
  public suiteMappingStatus: string[] = ['Yes','No'];
  public createdDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public updatedDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  maxDate = new Date();
  @Output('filterAction') filterEvent = new EventEmitter<string>();
  public disableStatus:boolean = false;
  public disableTestcaseType:boolean = false;
  public disablePriority:boolean = false;
  public disableRunResult:boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { version: WorkspaceVersion, filter: TestCaseFilter, query: string, isStepGroup:boolean },
    public translate: TranslateService,
    private testCaseTypeService: TestCaseTypesService,
    private testCasePriorityService: TestCasePrioritiesService,
    private authGuard: AuthenticationGuard,
    private testCaseTagService: TestCaseTagService) {
    this.isStepGroup = data.isStepGroup;
  }
  get resultConstant() {
    return Object.values(ResultConstant);
  }

  get statuses() {
    return Object.keys(TestCaseStatus);
  }

  ngOnInit() {
    this.createdDateRange.valueChanges.subscribe(res => {
      if(this.createdDateRange.valid){
        this.constructQueryString()
      }
    })
    this.updatedDateRange.valueChanges.subscribe(res => {
      if(this.updatedDateRange.valid){
        this.constructQueryString()
      }
    })
    if (this.data.query) {
      this.filterApplied = true;
      this.data.filter.normalizeCustomQuery(this.data.query);
    } else {
      this.data.filter.normalizeQuery(this.data.version.id);
    }
    this.splitQueryHash();
    this.fetchTestCaseTypes();
    this.fetchTestCasePriorities();
    this.fetchTags();
    this.disableSingleSelectedFields();
  }

  disableSingleSelectedFields(){
    if (this.filterStatuses.length > 0) this.disableStatus = true;
    if (this.filterTestCaseTypes.length > 0) this.disableTestcaseType = true;
    if (this.filterTestCasePriorities.length > 0) this.disablePriority = true;
    if (this.filterByResult.length >0) this.disableRunResult = true;
  }

  constructQueryString() {
    let queryString = "";
    if (this.filterTestCasePriorities?.length)
      queryString += ",priority@" + this.filterTestCasePriorities.join("#")
    if (this.filterStatuses?.length)
      queryString += ",status@" + this.filterStatuses.join("#")
    if (this.filterByResult?.length)
      queryString += ",result@" + this.filterByResult.join("#")
    if(this.filterIsMappedToSuite?.length) {
      if (this.filterIsMappedToSuite == 'Yes') queryString += ",suiteMapping:" + true;
      else if (this.filterIsMappedToSuite == 'No') queryString += ",suiteMapping:" + false;
    }
    if (this.createdDateRange?.valid) {
      queryString += ",createdDate>" + moment(this.createdDateRange.getRawValue().start).format("YYYY-MM-DD")
      queryString += ",createdDate<" + moment(this.createdDateRange.getRawValue().end).format("YYYY-MM-DD")
    }
    if (this.updatedDateRange?.valid) {
      queryString += ",updatedDate>" + moment(this.updatedDateRange.getRawValue().start).format("YYYY-MM-DD")
      queryString += ",updatedDate<" + moment(this.updatedDateRange.getRawValue().end).format("YYYY-MM-DD")
    }
    if (this.filterTestCaseTypes?.length)
      queryString += ",type@" + this.filterTestCaseTypes.join("#")
    if (this.filterTagIds?.length)
      queryString += ",tagId@" + this.filterTagIds.join("#")
    if(queryString)
    queryString += ",workspaceVersionId:" + this.filterWorkspaceVersionId
      + ",deleted:" + this.filterDeleted + ",isStepGroup:" + !!this.filterStepGroup;
    this.data.query = queryString;
  }

  filter() {
    this.filterApplied = !!this.data.query;
    this.filterEvent.emit(this.data.query);
  }

  reset() {
    this.filterApplied = false;
    this.filterTestCaseTypes = undefined;
    this.filterTestCasePriorities = undefined;
    this.filterStepGroup = undefined;
    this.filterDeleted = undefined;
    this.filterStatuses = undefined;
    this.filterByResult = undefined;
    this.updatedDateRange.controls['start'].setValue(undefined);
    this.updatedDateRange.controls['end'].setValue(undefined);
    this.createdDateRange.controls['start'].setValue(undefined);
    this.createdDateRange.controls['end'].setValue(undefined);
    this.data.filter.normalizeQuery(this.data.version.id);
    this.splitQueryHash();
    this.customFieldsQueryHash = [];
    this.data.query = undefined;
    this.filterEvent.emit(this.data.query);
    this.filterIsMappedToSuite = undefined;
  }

  convertToResultTypeFormat(result):string {
    result = result.replaceAll('_',' ').toLowerCase();
    return result.replace(result.charAt(0),result.charAt(0).toUpperCase());
  }

  private splitQueryHash() {
    if (this.data.filter.normalizedQuery.find(query => query.key == "status"))
      this.filterStatuses = <TestCaseStatus[]>this.data.filter.normalizedQuery.find(query => query.key == "status").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "result"))
      this.filterByResult = <ResultConstant[]>this.data.filter.normalizedQuery.find(query => query.key == "result").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "isStepGroup"))
      this.filterStepGroup = <boolean>this.data.filter.normalizedQuery.find(query => query.key == "isStepGroup").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "deleted") || this.data.filter.isDeleted)
      this.filterDeleted = <boolean>this.data.filter.normalizedQuery.find(query => query.key == "deleted").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "suiteMapping"))
      this.filterIsMappedToSuite = <string>this.data.filter.normalizedQuery.find(query => query.key == "suiteMapping").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "workspaceVersionId"))
      this.filterWorkspaceVersionId = <number>this.data.filter.normalizedQuery.find(query => query.key == "workspaceVersionId").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "priority"))
      this.filterTestCasePriorities = <number[]>this.data.filter.normalizedQuery.find(query => query.key == "priority").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "type"))
      this.filterTestCaseTypes = <number[]>this.data.filter.normalizedQuery.find(query => query.key == "type").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "tagId"))
      this.filterTagIds = <number[]>this.data.filter.normalizedQuery.find(query => query.key == "tagId").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN))
      this.createdDateRange.controls['end'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN))
      this.createdDateRange.controls['start'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN))
      this.updatedDateRange.controls['end'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN))
      this.updatedDateRange.controls['start'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));
  }

  private fetchTestCasePriorities() {
    this.testCasePriorityService.findAll("workspaceId:" + this.data.version.workspaceId).subscribe(res => {
      this.testCasePrioritiesList = res;
    });
  }

  private fetchTestCaseTypes(): void {
    this.testCaseTypeService.findAll("workspaceId:" + this.data.version.workspaceId).subscribe(res => {
      this.testCaseTypesList = res;
    });
  }

  private fetchTags(): void {
    this.testCaseTagService.findAll(undefined).subscribe(res => {
      this.tags = res;
    });
  }

  disableFilter() {
    return (!this.filterApplied && !this.data.query) ||
           this.dateInvalid(this.updatedDateRange) ||
           this.dateInvalid(this.createdDateRange);
  }

  dateInvalid(DateRange) {
    return ((DateRange.controls.start.value || DateRange.controls.start.errors?.matDatepickerParse?.text) ||
            (DateRange.controls.end.value || DateRange.controls.end.errors?.matDatepickerParse?.text) ) &&
            DateRange.invalid;
  }
}
