import {Component, Inject, OnInit, ViewChild, ElementRef} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {ElementFilter} from "../../models/element-filter.model";
import {Page} from "../../shared/models/page";
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import { fromEvent } from 'rxjs';
import { filter, debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';


@Component({
  selector: 'app-element-filters-list',
  templateUrl: './element-filters-list.component.html'
})

export class ElementFiltersListComponent implements OnInit {
  @ViewChild('searchInput') searchInput: ElementRef;
  public elementFilters: ElementFilter[];
  public currentFilter: ElementFilter;
  public version: WorkspaceVersion;
  public isEmptyDefault: boolean = false;
  public isEmptyNonDefault: boolean = false;
  inputValue:any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      list: Page<ElementFilter>, currentFilter: ElementFilter, version: WorkspaceVersion
    }) {
  }

  ngOnInit() {
    this.elementFilters = this.data.list.content;
    this.currentFilter = this.data.currentFilter;
    this.version = this.data.version;
    this.addSearchTestCaseFilterListEvent()
  }

  get nonDefaultFilters() {
    return [];
  }

  get defaultFilter() {
    return this.elementFilters;
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
            let defaultFilter = this.elementFilters.filter(filer => {
              if(filer.isDefault && filer.name.includes(term)) {
                return true
              }
            })
            this.isEmptyDefault = !!!defaultFilter.length;
            let NonDefaultFilter = this.elementFilters.filter(filer => {
              if(filer.name.includes(term)) {
                return true
              }
            })
            this.isEmptyNonDefault = !!!NonDefaultFilter.length;
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

}
