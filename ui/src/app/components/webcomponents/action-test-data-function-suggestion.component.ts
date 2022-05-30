import {Component, ElementRef, OnInit, ViewChild, Inject, Input, Optional} from '@angular/core';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {DefaultDataGeneratorService} from '../../services/default-data-generator.service';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {InfiniteScrollableDataSource} from '../../data-sources/infinite-scrollable-data-source';
import {AddonTestDataFunctionService} from "../../services/addon-default-data-generator.service";
import {AddonDetailsComponent} from "../../shared/components/webcomponents/addon-details.component";
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";
import {TestDataType} from "../../enums/test-data-type.enum";

@Component({
  selector: 'app-action-test-data-function-suggestion',
  templateUrl: './action-test-data-function-suggestion.component.html',
  styles: []
})
export class ActionTestDataFunctionSuggestionComponent implements OnInit {
  @Optional() @Input('testDataCustomFunction') testDataCustomFunction;
  public activeTab = 'custom_functions'
  public testDataFunctionSuggestion: InfiniteScrollableDataSource;
  public addonCustomFunctionSuggestion: InfiniteScrollableDataSource;
  public searchText: string;
  @ViewChild('searchInput') searchInput: ElementRef;
  public versionId: number;
  public showVideo: Boolean = false;
  public isQueryBased = false;
  public currentFocusedIndex = 0;

  constructor(
    private dialogRef: MatDialogRef<ActionTestDataFunctionSuggestionComponent, any>,
    @Inject(MAT_DIALOG_DATA) public option: { versionId: number },
    private defaultDataGeneratorService: DefaultDataGeneratorService,
    private addonTestDataFunctionService: AddonTestDataFunctionService,
    private mobileRecorderEventService: MobileRecorderEventService,
    private matModal: MatDialog
  ) {
    this.versionId = this.option.versionId;
  }

  ngOnInit(): void {
    if (this.testDataCustomFunction) {
      this.option = this.testDataCustomFunction;
      this.versionId = this.option.versionId;
    }
    this.fetchTestDataFunction();
    this.fetchAddonTestDataFunctions();
    this.attachDebounceEvent();
    setTimeout(() => {
      this.searchInput.nativeElement.focus();
    }, 300);
  }

  fetchAddonTestDataFunctions(isSearch?){
    let searchQuery = '';
    if (isSearch) {
      this.isQueryBased = true;
      searchQuery = ',displayName:*' + isSearch + '*';
    } else {
      this.isQueryBased = false;
    }
    this.addonCustomFunctionSuggestion = new InfiniteScrollableDataSource(this.addonTestDataFunctionService,   searchQuery, undefined, 50);
  }

  openAddonDetails(id){
    const dialog = this.matModal.open(AddonDetailsComponent, {
      height: "100vh",
      width: '30%',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        functionId: id
      }
    });
    dialog.afterClosed().subscribe(data => {
    });
  }
  canShowCustomFunctionsParameters(displayNames){
    return displayNames ? Object.keys(displayNames).length : 0;
  }

  fetchTestDataFunction(isSearch?) {
    let searchQuery = '';
    if (isSearch) {
      this.isQueryBased = true;
      searchQuery = ',term:*' + isSearch + '*';
    } else {
      this.isQueryBased = false;
    }
    this.testDataFunctionSuggestion = new InfiniteScrollableDataSource(this.defaultDataGeneratorService, searchQuery, undefined, 50);
  }

  attachDebounceEvent() {
    if (this.searchInput && this.searchInput.nativeElement) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(200),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (['ArrowDown', 'ArrowUp'].includes(event.key)) {
              return;
            }
            if (this.environmentNameValue) {
              this.searchText = this.environmentNameValue;
              this.fetchTestDataFunction(this.searchText);
              this.fetchAddonTestDataFunctions(this.searchText);
            } else {
              this.fetchTestDataFunction();
              this.fetchAddonTestDataFunctions();
            }
          })
        )
        .subscribe();
      this.searchInput.nativeElement.focus();
    } else {
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
    }
  }

  get environmentNameValue() {
    return this.searchInput.nativeElement.innerHTML.replace(/<br>/g, '');
  }

  selectedSuggestion(suggestion?) {
    suggestion = suggestion || this.testDataFunctionSuggestion.cachedItems[this.currentFocusedIndex] || this.addonCustomFunctionSuggestion.cachedItems[this.currentFocusedIndex];
      this.testDataCustomFunction ? this.mobileRecorderEventService.returnData.next({type: TestDataType.function, data: suggestion}) : this.dialogRef.close(suggestion);
  }

  scrollUpFunctionFocus() {
    if (this.currentFocusedIndex) {
      --this.currentFocusedIndex;
    }
    const target = document.querySelector('.h-active') as HTMLElement;
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  scrollDownFunctionFocus() {
    if (this.currentFocusedIndex < this.testDataFunctionSuggestion.totalElements || this.currentFocusedIndex < this.addonCustomFunctionSuggestion.totalElements) {
      ++this.currentFocusedIndex;
    }
    const target = document.querySelector('.h-active') as HTMLElement;
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  upgradeCheck(element) {
    return false;
  }

  isFetching(){
    return !this.testDataFunctionSuggestion?.isEmpty || this.isQueryBased || this.testDataFunctionSuggestion?.isFetching;
  }

  searchCriteriaNotFound(){
    return (this.activeTab=='custom_functions' && this.testDataFunctionSuggestion?.isEmpty) || (this.activeTab=='addon_functions' && this.addonCustomFunctionSuggestion?.isEmpty);
  }

  closeSuggestion(data?) {
    this.testDataCustomFunction ? this.mobileRecorderEventService.setEmptyAction() : this.dialogRef.close(data);
  }
}
