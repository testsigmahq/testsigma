import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {TestDataService} from "../../services/test-data.service";
import {TestDataSet} from "../../models/test-data-set.model";

@Component({
  selector: 'app-action-test-data-parameter-suggestion',
  templateUrl: './action-test-data-parameter-suggestion.component.html',
  styles: []
})
export class ActionTestDataParameterSuggestionComponent implements OnInit {
  public dataProfileSuggestion: any[];
  public filteredSuggestion: any[];
  public searchText: string;
  public testDataName: String = "";
  public currentFocusedIndex: number = 0;
  public showVideo: Boolean = false;
  public isQueryBased: boolean = false;
  public versionId: number;
  public testCaseId: number;
  @ViewChild('searchInput') searchInput: ElementRef;

  constructor(
    private dialogRef: MatDialogRef<ActionTestDataParameterSuggestionComponent, any>,
    private testDataService: TestDataService,
    @Inject(MAT_DIALOG_DATA) public option: { dataProfileId?: number, versionId: number, testCaseId: number, stepRecorderView: boolean },
  ) {
    this.versionId = this.option.versionId;
    this.testCaseId = this.option.testCaseId;
  }

  ngOnInit(): void {
    this.fetchDataParameter();
    this.attachDebounceEvent();
  }

  fetchDataParameter() {
    if(this.option.dataProfileId)
    this.testDataService.show(this.option.dataProfileId).subscribe(res => {
      this.dataProfileSuggestion = [];
      this.testDataName = res.name;
      res.data.forEach((dataSet: TestDataSet) => {
        for (let key in dataSet.data) {
          this.dataProfileSuggestion.push(key)
        }
      })
      this.dataProfileSuggestion = [...new Set(this.dataProfileSuggestion)];
      this.filter()
    })
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
            if (this.setName) {
              this.searchText = this.setName;
              this.filter(this.searchText);
            } else {
              this.searchText = '';
              this.filteredSuggestion = this.dataProfileSuggestion;
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

  get setName() {
    return this.searchInput.nativeElement.innerHTML.replace(/<br>/g, "");
  }

  filter(searchText?: string) {
    if (searchText && searchText.length) {
      this.filteredSuggestion = [];
      this.dataProfileSuggestion.forEach(suggestion => {
        if (suggestion.toLowerCase().includes(searchText.toLowerCase())) {
          this.filteredSuggestion.push(suggestion);
        }
      })
      this.filteredSuggestion = [...new Set(this.filteredSuggestion)];
    } else if (!searchText) {
      this.filteredSuggestion = this.dataProfileSuggestion;
    }
  }


  selectedSuggestion(suggestion?: any) {
    suggestion = suggestion || this.filteredSuggestion[this.currentFocusedIndex];
    this.dialogRef.close(suggestion);
  }

  scrollUpParameterFocus() {
    if (this.currentFocusedIndex)
      --this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  scrollDownParameterFocus() {
    if (this.currentFocusedIndex < this.filteredSuggestion.length - 1)
      ++this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }
}
