import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {TestCaseTag} from "../../models/test-case-tag.model";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseFilter} from "../../models/test-case-filter.model";
import * as moment from "moment";
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestSuiteTagService} from "../../services/test-suite-tag.service";
import {FilterQuery} from "../../models/filter-query";
import {FilterOperation} from "../../enums/filter.operation.enum";

@Component({
  selector: 'app-filter-form',
  templateUrl: './filter-form.component.html',
  styles: [
  ]
})
export class FilterFormComponent implements OnInit {

  public filterApplied: boolean;
  public filterName: string = '';
  public filterTestCaseName: string = '';
  public filterCreatedBy: number[];
  public filterTestCases: number[];
  public filterByStatusResult: ResultConstant[];
  public createdDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public updatedDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public lastRunDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public normalizedQuery: FilterQuery[];
  maxDate = new Date();
  public tags: TestCaseTag[];
  public filterTagIds: number[];
  @Output('filterAction') filterEvent = new EventEmitter<string>();
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { version: WorkspaceVersion, filter: TestCaseFilter, query: string, isStepGroup:boolean },
    private testSuiteTagService: TestSuiteTagService
  ) { }

  ngOnInit(): void {
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
    this.lastRunDateRange.valueChanges.subscribe(res => {
      if(this.lastRunDateRange.valid){
        this.constructQueryString()
      }
    })
    if(this.data?.query) {
      this.filterApplied = true;
      this.normalizeCustomQuery(this.data.query);
    }
    this.splitQueryHash();
    this.fetchTags();
  }

  setFilteredTestCases($event){
    this.filterTestCases = $event;
    this.constructQueryString();
  }

  private fetchTags() {
    console.log("====>fetchTags")
    this.testSuiteTagService.findAll(undefined).subscribe(res => {
      this.tags = res;
    });
  }

  get resultStatusConstant() {
    return Object.values(ResultConstant);
  }

  convertToResultTypeFormat(result):string {
    result = result.replaceAll('_',' ').toLowerCase();
    return result.replace(result.charAt(0),result.charAt(0).toUpperCase());
  }

  normalizeCustomQuery(queryHash: string) {
    let normalizedQueryHash: FilterQuery[] = [];
    if(queryHash){
      queryHash.split(",").forEach(query => {
        let normalizedQuery = new FilterQuery(), separator, secondSeparator, operation;
        if (query == '') return;
        if (query.indexOf(":") > 0) {
          separator = ":"; operation = FilterOperation.EQUALITY;
        } else if (query.indexOf(">") > 0) {
          separator = ">"; operation = FilterOperation.GREATER_THAN;
        } else if (query.indexOf("<") > 0) {
          separator = "<"; operation = FilterOperation.LESS_THAN;
        } else if (query.indexOf("@") > 0) {
          separator = "@"; secondSeparator = "#"; operation = FilterOperation.IN;
        }
        normalizedQuery.operation = operation;
        normalizedQuery.key = query.split(separator)[0];

        if (secondSeparator) {
          normalizedQuery.value = query.split(separator)[1].split(secondSeparator);
          if (normalizedQuery.key != "locatorType")
            normalizedQuery.value = normalizedQuery.value.map(v => v);
        } else {
          const value = query.split(separator)[1];
          if(value?.startsWith("*") && value?.endsWith("*")){
            normalizedQuery.value = value.substring( value.indexOf("*") + 1, value.lastIndexOf("*") );
          }else {
            normalizedQuery.value = query.split(separator)[1];
          }
        }

        normalizedQueryHash.push(normalizedQuery);
      });
    }
    this.normalizedQuery = normalizedQueryHash;
  }

  private splitQueryHash() {
    if (this.normalizedQuery?.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN))
      this.createdDateRange.controls['end'].setValue(moment(<number>this.normalizedQuery?.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.normalizedQuery?.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN))
      this.createdDateRange.controls['start'].setValue(moment(<number>this.normalizedQuery?.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));

    if (this.normalizedQuery?.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN))
      this.updatedDateRange.controls['end'].setValue(moment(<number>this.normalizedQuery?.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.normalizedQuery?.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN))
      this.updatedDateRange.controls['start'].setValue(moment(<number>this.normalizedQuery?.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));

    if (this.normalizedQuery?.find(query => query.key == "lastRunOn" && query.operation == FilterOperation.LESS_THAN))
      this.lastRunDateRange.controls['end'].setValue(moment(<number>this.normalizedQuery?.find(query => query.key == "lastRunOn" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.normalizedQuery?.find(query => query.key == "lastRunOn" && query.operation == FilterOperation.GREATER_THAN))
      this.lastRunDateRange.controls['start'].setValue(moment(<number>this.normalizedQuery?.find(query => query.key == "lastRunOn" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));

    if (this.normalizedQuery?.find(query => query.key == "lastRunResult"))
      this.filterByStatusResult = <ResultConstant[]>this.normalizedQuery.find(query => query.key == "lastRunResult").value;

    if(this.normalizedQuery?.find(query => query.key == "createdBy"))
      this.filterCreatedBy = <number[]>this.normalizedQuery.find(query => query.key == "createdBy")?.value;
    if(this.filterCreatedBy?.length){
      this.filterCreatedBy = this.filterCreatedBy.map(v=> parseInt(String(v)));
    }

    if(this.normalizedQuery?.find(query => query.key == "testcaseId"))
      this.filterTestCases = <number[]>this.normalizedQuery.find(query => query.key == "testcaseId")?.value;
    if(this.filterTestCases?.length){
      this.filterTestCases = this.filterTestCases.map(v=> parseInt(String(v)));
    }

    if (this.normalizedQuery?.find(query => query.key == "tagId"))
      this.filterTagIds = <number[]>this.normalizedQuery.find(query => query.key == "tagId").value;
    if(this.filterTagIds?.length){
      this.filterTagIds = this.filterTagIds.map(v=> parseInt(String(v)));
    }

    if (this.normalizedQuery?.find(query => query.key == "testCaseName"))
      this.filterTestCaseName = <string>this.normalizedQuery.find(query => query.key == "testCaseName").value;

    if (this.normalizedQuery?.find(query => query.key == "name"))
      this.filterName = <string>this.normalizedQuery.find(query => query.key == "name").value;
  }

  constructQueryString() {
    let queryString = "";

    if (this.createdDateRange?.valid) {
      queryString += ",createdDate>" + moment(this.createdDateRange.getRawValue().start).format("YYYY-MM-DD");
      queryString += ",createdDate<" + moment(this.createdDateRange.getRawValue().end).format("YYYY-MM-DD");
    }
    if (this.updatedDateRange?.valid) {
      queryString += ",updatedDate>" + moment(this.updatedDateRange.getRawValue().start).format("YYYY-MM-DD");
      queryString += ",updatedDate<" + moment(this.updatedDateRange.getRawValue().end).format("YYYY-MM-DD");
    }
    if(this.lastRunDateRange?.valid){
      queryString += ",lastRunOn>" + moment(this.lastRunDateRange.getRawValue().start).format("YYYY-MM-DD");
      queryString += ",lastRunOn<" + moment(this.lastRunDateRange.getRawValue().end).format("YYYY-MM-DD");
    }
    if(this.filterName){
      queryString +=",name:*"+this.filterName+"*";
    }
    console.log("====>this.filterTestCaseName",this.filterTestCaseName);
    if(this.filterTestCaseName){
      queryString += ",testCaseName:*"+this.filterTestCaseName+"*,testSuiteApplicationVersionId:"+this.data?.version?.id;
    }
    if( this.filterCreatedBy?.length ){
      queryString +=",createdBy@"+this.filterCreatedBy.join("#");
    }
    if( this.filterTestCases?.length){
      queryString +=",testcaseId@"+this.filterTestCases.join("#");
    }
    if (this.filterByStatusResult?.length)
      queryString += ",lastRunResult@" + this.filterByStatusResult.join("#")
    if (this.filterTagIds?.length)
      queryString += ",tagId@" + this.filterTagIds.join("#")
    this.data.query = queryString;
  }

  filter() {
    this.filterApplied = !!this.data?.query;
    this.filterEvent.emit(this.data.query);
  }

  reset() {
    this.filterName=undefined;
    this.filterApplied = false;
    this.filterCreatedBy = undefined;
    this.filterTestCases=undefined;
    this.filterTagIds = undefined;
    this.filterByStatusResult = undefined;
    this.updatedDateRange.controls['start'].setValue(undefined);
    this.updatedDateRange.controls['end'].setValue(undefined);
    this.createdDateRange.controls['start'].setValue(undefined);
    this.createdDateRange.controls['end'].setValue(undefined);
    this.lastRunDateRange.controls['start'].setValue(undefined);
    this.lastRunDateRange.controls['end'].setValue(undefined);
    this.data.query = undefined;
    this.filterEvent.emit(this.data.query);
    this.normalizeCustomQuery(undefined);
    this.splitQueryHash();
  }

  disableFilter() {
    return (!this.filterApplied && !this.data?.query) ||
      this.dateInvalid(this.updatedDateRange) ||
      this.dateInvalid(this.createdDateRange) || this.dateInvalid(this.lastRunDateRange);
  }

  dateInvalid(DateRange) {
    return ((DateRange.controls.start.value || DateRange.controls.start.errors?.matDatepickerParse?.text) ||
        (DateRange.controls.end.value || DateRange.controls.end.errors?.matDatepickerParse?.text) ) &&
      DateRange.invalid;
  }
}
