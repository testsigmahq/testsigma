import {Component, EventEmitter, Inject, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ElementFilter} from "../../models/element-filter.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {ElementLocatorType} from "../../enums/element-locator-type.enum";
import {ElementTagService} from "../../services/element-tag.service";
import {ElementTag} from "../../models/element-tag.model";
import {FormControl, FormGroup} from "@angular/forms";
import * as moment from "moment";
import {FilterOperation} from "../../enums/filter.operation.enum";

@Component({
  selector: 'app-element-filter',
  templateUrl: './element-filters.component.html'
})

export class ElementFiltersComponent implements OnInit {

  @Output('filterAction') filterAction = new EventEmitter<string>();

  public filterApplied: Boolean;
  public elementLocatorType = ElementLocatorType;
  public tags: ElementTag[];
  public usageStates: string[] = ['Yes','No'];

  public filterName: string = '';
  public filterLocatorTypes: ElementLocatorType[];
  public filterScreenName: string = '';
  public filterDefinition: string = '';
  public filterTagIds: number[];
  public filterByUsage: string = '';
  public createdDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public updatedDateRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });
  public maxDate = new Date();


  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { filter: ElementFilter, version: WorkspaceVersion, query: string },
    private elementTagService: ElementTagService,
    public authGuard: AuthenticationGuard) {}

  ngOnInit() {

    if (this.data) {
      if (this.data.query) {
        this.filterApplied = true;
        this.data.filter.normalizeCustomQuery(this.data.query);
      } else {
        this.data.filter.normalizeQuery(this.data.version.id);
      }
    }
    this.createdDateRange.valueChanges.subscribe(res => {
      if(this.createdDateRange.valid){
        this.constructQueryString()
      }
    })
    this.updatedDateRange.valueChanges.subscribe(res => {
      if(this.updatedDateRange?.valid){
        this.constructQueryString()
      }
    })
    this.splitQueryHash();
    this.fetchTags();
  }

  filter() {
    this.filterApplied = !!this.data.query;
    this.filterAction.emit(this.data.query);
  }

  constructQueryString() {
    let queryString = "";
    if (this.filterDefinition)
      queryString += ",locatorValue:*" + encodeURIComponent(this.filterDefinition) + "*";
    if (this.filterScreenName)
      queryString += ",screenName:*" + encodeURIComponent(this.filterScreenName) + "*"
    if (this.filterName)
      queryString += ",name:*" + encodeURIComponent(this.filterName) + "*"
    if (this.filterLocatorTypes?.length)
      queryString += ",locatorType@" + this.filterLocatorTypes.join("#")
    if (this.createdDateRange?.valid) {
      queryString += ",createdDate>" + moment(this.createdDateRange.getRawValue().start).format("YYYY-MM-DD")
      queryString += ",createdDate<" + moment(this.createdDateRange.getRawValue().end).format("YYYY-MM-DD")
    }
    if (this.updatedDateRange?.valid) {
      queryString += ",updatedDate>" + moment(this.updatedDateRange.getRawValue().start).format("YYYY-MM-DD")
      queryString += ",updatedDate<" + moment(this.updatedDateRange.getRawValue().end).format("YYYY-MM-DD")
    }
    if (this.filterTagIds?.length)
      queryString += ",tagId@" + this.filterTagIds.join("#")
    if(this.filterByUsage){
      if(this.filterByUsage==='No') queryString += ",isUsed:" + false;
      else if(this.filterByUsage==='Yes') queryString += ",isUsed:" + true;
    }
    if(queryString)
      queryString = queryString.slice(1);
    this.data.query = queryString;
  }

  reset() {
    this.filterApplied = false;
    this.filterDefinition = '';
    this.filterScreenName = '';
    this.filterName = '';
    this.filterLocatorTypes = undefined;
    this.updatedDateRange.controls['start'].setValue(undefined);
    this.updatedDateRange.controls['end'].setValue(undefined);
    this.createdDateRange.controls['start'].setValue(undefined);
    this.createdDateRange.controls['end'].setValue(undefined);
    this.filterTagIds = undefined;
    this.filterByUsage = '';
    this.data.filter.normalizeQuery(this.data.version.id);
    this.splitQueryHash();
    this.data.query = undefined;
    this.filterAction.emit(this.data.query);
  }

  private splitQueryHash() {
    if (this.data.filter.normalizedQuery.find(query => query.key == "locatorType"))
      this.filterLocatorTypes = <ElementLocatorType[]>this.data.filter.normalizedQuery.find(query => query.key == "locatorType").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "name")) {
      this.filterName = <string>this.data.filter.normalizedQuery.find(query => query.key == "name").value;
      this.filterName = this.filterName.split("*")[1];
      this.filterName = decodeURIComponent(this.filterName);
    }
    if (this.data.filter.normalizedQuery.find(query => query.key == "screenName")) {
      this.filterScreenName = <string>this.data.filter.normalizedQuery.find(query => query.key == "screenName").value;
      this.filterScreenName =  this.filterScreenName.includes("*")? this.filterScreenName.split("*")[1] : this.filterScreenName;
      this.filterScreenName = decodeURIComponent(this.filterScreenName);
    }
    if (this.data.filter.normalizedQuery.find(query => query.key == "definition")) {
      this.filterDefinition = <string>this.data.filter.normalizedQuery.find(query => query.key == "definition").value;
      this.filterDefinition = this.filterDefinition.split("*")[1];
    }
    if (this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN))
      this.createdDateRange.controls['end'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN))
      this.createdDateRange.controls['start'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN))
      this.updatedDateRange.controls['end'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN))
      this.updatedDateRange.controls['start'].setValue(moment(<number>this.data.filter.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD"));
    if (this.data.filter.normalizedQuery.find(query => query.key == "tagId"))
      this.filterTagIds = <number[]>this.data.filter.normalizedQuery.find(query => query.key == "tagId").value;
    if (this.data.filter.normalizedQuery.find(query => query.key == "isUsed"))
      this.filterByUsage = <string>this.data.filter.normalizedQuery.find(query => query.key == "isUsed").value;
    this.filterByUsage = (this.filterByUsage != null && this.filterByUsage.length) ? this.filterByUsage=='true' ? 'Yes' : 'No' : '';
  }

  private fetchTags() {
    this.elementTagService.findAll(undefined).subscribe(res => {
      this.tags = res;
    });
  }

  dateInvalid(DateRange) {
    return ((DateRange.controls.start.value || DateRange.controls.start.errors?.matDatepickerParse?.text) ||
      (DateRange.controls.end.value || DateRange.controls.end.errors?.matDatepickerParse?.text) ) &&
      DateRange.invalid;
  }

  disableFilter() {
    return (!this.filterApplied && !this.data.query) ||
      this.dateInvalid(this.updatedDateRange) ||
      this.dateInvalid(this.updatedDateRange);
  }

}
