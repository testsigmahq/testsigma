import {Component, ElementRef, OnInit, ViewChild, Inject} from '@angular/core';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {DefaultDataGeneratorService} from '../../services/default-data-generator.service';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {InfiniteScrollableDataSource} from '../../data-sources/infinite-scrollable-data-source';
import {KibbutzTestDataFunctionService} from "../../services/kibbutz-default-data-generator.service";
import {AddonDetailsComponent} from "../../shared/components/webcomponents/addon-details.component";

@Component({
  selector: 'app-action-test-data-function-suggestion',
  templateUrl: './action-test-data-function-suggestion.component.html',
  styles: []
})
export class ActionTestDataFunctionSuggestionComponent implements OnInit {
  public activeTab = 'custom_functions'
  public testDataFunctionSuggestion: InfiniteScrollableDataSource;
  public kibbutzCustomFunctionSuggestion: InfiniteScrollableDataSource;
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
    private kibbutzTestDataFunctionService: KibbutzTestDataFunctionService,
    private matModal: MatDialog
  ) {
    this.versionId = this.option.versionId;
  }

  ngOnInit(): void {
    this.fetchTestDataFunction();
    this.fetchKibbutzTestDataFunctions();
    this.attachDebounceEvent();
    setTimeout(() => {
      this.searchInput.nativeElement.focus();
    }, 300);
  }

  fetchKibbutzTestDataFunctions(isSearch?){
    let searchQuery = '';
    if (isSearch) {
      this.isQueryBased = true;
      searchQuery = ',displayName:*' + isSearch + '*';
    } else {
      this.isQueryBased = false;
    }
    this.kibbutzCustomFunctionSuggestion = new InfiniteScrollableDataSource(this.kibbutzTestDataFunctionService,   searchQuery, undefined, 50);
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
              this.fetchKibbutzTestDataFunctions(this.searchText);
            } else {
              this.fetchTestDataFunction();
              this.fetchKibbutzTestDataFunctions();
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
    suggestion = suggestion || this.testDataFunctionSuggestion.cachedItems[this.currentFocusedIndex] || this.kibbutzCustomFunctionSuggestion.cachedItems[this.currentFocusedIndex];
    this.dialogRef.close(suggestion);
  }

  scrollUpFunctionFocus() {
    if (this.currentFocusedIndex) {
      --this.currentFocusedIndex;
    }
    const target = document.querySelector('.h-active') as HTMLElement;
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  scrollDownFunctionFocus() {
    if (this.currentFocusedIndex < this.testDataFunctionSuggestion.totalElements || this.currentFocusedIndex < this.kibbutzCustomFunctionSuggestion.totalElements) {
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
    return (this.activeTab=='custom_functions' && this.testDataFunctionSuggestion?.isEmpty) || (this.activeTab=='kibbutz_functions' && this.kibbutzCustomFunctionSuggestion?.isEmpty);
  }
}
