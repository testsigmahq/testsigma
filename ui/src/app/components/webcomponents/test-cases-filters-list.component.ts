/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {fromEvent} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {Page} from "../../shared/models/page";
import {TestCaseFilter} from "../../models/test-case-filter.model";
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {StepGroupFilter} from "../../models/step-group-filter.model";

@Component({
  selector: 'app-filter-list',
  templateUrl: './test-cases-filters-list.component.html',
  styles: []
})
export class TestCasesFiltersListComponent implements OnInit {
  @ViewChild('searchInput') searchInput: ElementRef;

  public filters: TestCaseFilter[];
  public stepGroupFilters: StepGroupFilter[];
  public currentFilter: TestCaseFilter | StepGroupFilter;
  public version: WorkspaceVersion;
  inputValue:any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      list: Page<TestCaseFilter> | Page<StepGroupFilter>, currentFilter: TestCaseFilter|StepGroupFilter, version: WorkspaceVersion,isStepGroups:boolean
    },
    public translate: TranslateService) {
  }

  get isStepGroup() {
    return this.data.currentFilter instanceof StepGroupFilter;
  }

  get urlString() {
    return this.isStepGroup ? 'step_groups' : 'cases';
  }

  ngOnInit() {
    if(this.isStepGroup)
      this.stepGroupFilters = <StepGroupFilter[]>this.filterContent
    else
      this.filters = <TestCaseFilter[]>this.filterContent;
    this.currentFilter = this.data.currentFilter;
    this.version = this.data.version;
    this.addSearchTestCaseFilterListEvent();
  }

  get filterContent(){
    if(this.isStepGroup)
      return (<Page<StepGroupFilter>>this.data.list).content
    else
      return (<Page<TestCaseFilter>>this.data.list).content;
  }

  addSearchTestCaseFilterListEvent() {
    if (this.searchInput) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            let term = this.searchInput.nativeElement.value;
            if(this.isStepGroup){
              this.stepGroupFilters = (<StepGroupFilter[]>this.data.list.content).filter(filter => {
                return filter.name.toUpperCase().indexOf(term.toUpperCase()) > -1;
              });
            } else {
              this.filters = (<TestCaseFilter[]>this.data.list.content).filter(filter => {
                return filter.name.toUpperCase().indexOf(term.toUpperCase()) > -1;
              });
            }
          })
        )
        .subscribe();
    } else {
      setTimeout(() => {
        this.addSearchTestCaseFilterListEvent()
      }, 100);
    }
  }

  focusOnSearch() {
    this.searchInput.nativeElement.focus();
  }

  get nonDefaultFilters() {
    return []
  }

  get defaultFilters() {
    return this.isStepGroup ? this.stepGroupFilters : this.filters
  }
}
