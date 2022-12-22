import {Component, ElementRef, Inject, Input, OnInit, Optional, ViewChild} from '@angular/core';
import {Page} from "../../shared/models/page";
import {Environment} from "../../models/environment.model";
import {EnvironmentService} from "../../services/environment.service";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";
import {TestDataType} from "../../enums/test-data-type.enum";

@Component({
  selector: 'app-action-test-data-environment-suggestion',
  templateUrl: './action-test-data-environment-suggestion.component.html',
  styles: []
})
export class ActionTestDataEnvironmentSuggestionComponent implements OnInit {
  @Optional() @Input('testDataEnvironment') testDataEnvironment;
  public environments: any[];
  public filteredSuggestion: any[];
  public currentFocusedIndex: number = 0;
  public  versionId: number;
  public showVideo: Boolean = false;
  public isQueryBased: boolean = false;
  @ViewChild('searchInput') searchInput: ElementRef;
  public isFetching: boolean;
  get getSource() {
    return "https://s3.amazonaws.com/assets.testsigma.com/videos/environments/create.mp4";
  }

  constructor(
    private dialogRef: MatDialogRef<ActionTestDataEnvironmentSuggestionComponent, any>,
    private environmentService: EnvironmentService,
    @Inject(MAT_DIALOG_DATA) public option: { versionId: number, stepRecorderView: boolean },
    private mobileRecorderEventService: MobileRecorderEventService
  ) {
  }

  ngOnInit(): void {
    if (this.testDataEnvironment) {
      this.option = this.testDataEnvironment;
    }
    this.versionId = this.option.versionId;
    this.fetchEnvironment();
    this.attachDebounceEvent();
    setTimeout(()=> {
      this.searchInput?.nativeElement?.focus();
    }, 300);
  }

  private fetchEnvironment() {
    this.isFetching = true;
    this.environmentService.findAll()
      .subscribe((res: Page<Environment>) => {
        if(!res.empty) {
          this.environments = [];
          res.content.forEach(item => {
            for (let key in item.parameters) {
              this.environments.push({'parameter':key, 'name': item.name});
            }
          })
          this.environments = [...new Set(this.environments)];
          this.filteredSuggestion = this.environments;
        }
        this.isFetching = false;
      }, error => {
        this.isFetching = false;
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
            let searchText = this.environmentNameValue;
            if (searchText) {
              this.isQueryBased = true;
              this.filter(searchText);
            } else {
              this.isQueryBased = false;
              this.filteredSuggestion = this.environments;
            }
          })
        )
        .subscribe();
      this.searchInput.nativeElement.focus();
    } else
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
  }

  get environmentNameValue() {
    return this.searchInput.nativeElement.innerHTML.replace(/<br>/g, "");
  }

  filter(searchText?: string) {
    let filteredSuggestion = [];
    if (searchText && searchText.length) {
      this.environments.forEach(suggestion => {
        if (suggestion.parameter?.toLowerCase().includes(searchText.toLowerCase())) {
          filteredSuggestion.push(suggestion);
        }
      })
      filteredSuggestion = [...new Set(filteredSuggestion)];
    } else if (!searchText) {
      filteredSuggestion = this.environments;
    }
    this.filteredSuggestion = [...new Set(filteredSuggestion)];
  }

  selectedSuggestion(suggestion?: any) {
    suggestion = suggestion || this.filteredSuggestion[this.currentFocusedIndex];
    this.testDataEnvironment ? this.mobileRecorderEventService.returnData.next({type: TestDataType.global, data: suggestion}) : this.dialogRef.close(suggestion);
  }

  scrollUpEnvParamFocus() {
    if (this.currentFocusedIndex)
      --this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  scrollDownEnvParamFocus() {
    if (this.currentFocusedIndex < this.filteredSuggestion.length - 1)
      ++this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  closeSuggestion(data?)  {
    this.testDataEnvironment ? this.mobileRecorderEventService.setEmptyAction() : this.dialogRef.close(data);
  }
}
