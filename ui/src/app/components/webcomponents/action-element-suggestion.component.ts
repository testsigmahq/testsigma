import {Component, ElementRef, Inject, Input, OnInit, Optional, ViewChild} from '@angular/core';
import {ElementService} from "../../shared/services/element.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {MAT_DIALOG_DATA, MatDialogRef, MatDialog} from '@angular/material/dialog';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {Element} from "../../models/element.model";
import {Pageable} from "../../shared/models/pageable";
import {ElementFormComponent} from "./element-form.component";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCase} from "../../models/test-case.model";
import {ActionTestDataFunctionSuggestionComponent} from "./action-test-data-function-suggestion.component";
import {ActionTestDataParameterSuggestionComponent} from "./action-test-data-parameter-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "./action-test-data-environment-suggestion.component";
import {TestStepMoreActionFormComponent} from "./test-step-more-action-form.component";
import {MobileStepRecorderComponent} from "../../agents/components/webcomponents/mobile-step-recorder.component";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";

@Component({
  selector: 'app-action-element-suggestion',
  templateUrl: './action-element-suggestion.component.html',
  styles: [
  ]
})
export class ActionElementSuggestionComponent implements OnInit {
  @Optional() @Input('elementSuggestion') elementSuggestion;
  public workspaceVersion: WorkspaceVersion;
  public elements: InfiniteScrollableDataSource;
  public filteredElements: Element[];
  public isNew: Boolean = false;
  public searchText: string;
  @ViewChild('searchInput') searchInput: ElementRef;
  @ViewChild('searchScreenInput') searchScreenInput: ElementRef;
  public currentFocusedIndex: number;
  private elementForm: MatDialogRef<ElementFormComponent>;
  public showVideo: Boolean = false;
  public isQueryBased: boolean = false;

  constructor(
    private dialogRef: MatDialogRef<ActionElementSuggestionComponent>,
    private elementService: ElementService,
    private matModal: MatDialog,
    private mobileRecorderEventService: MobileRecorderEventService,
    @Inject(MAT_DIALOG_DATA) public option: { version: WorkspaceVersion, testCase: TestCase, testCaseResultId?:number, isDryRun: boolean, isStepRecordView: boolean, previousStepElementName?:string, currentStepElementName?:string},
  ) {
    this.workspaceVersion = this.option?.version;
  }

  ngOnInit(): void {
    if(this.elementSuggestion) {
      this.option = this.elementSuggestion;
      this.workspaceVersion = this.option?.version;
    }
    this.fetchElement();
    this.attachDebounceEvent();
    this.attachScreenDebounceEvent();
  }

  fetchElement(term?:string) {
    let pageable = new Pageable();
    pageable.pageSize = 50;
    let searchName = '';
    if (term) {
      searchName = ",name:*" + term + "*";
      this.isQueryBased = true;
    } else {
      this.isQueryBased = false
    }
    if(this.option.previousStepElementName || this.option.currentStepElementName ){
      searchName += ",previousStepElementName:" + (this.option.currentStepElementName || this.option.previousStepElementName);
    }

    this.elements = new InfiniteScrollableDataSource(this.elementService, "workspaceVersionId:"+this.option.version.id+searchName, undefined, 50);
    this.setNewElement(term);
    // this.elementService.findAll("workspaceVersionId:"+this.option.version.id+searchName, undefined, pageable).subscribe(res => {
    //   this.filteredElements = res.content;
    // })
  }

  fetchElementByScreenName(term?:string) {
    let pageable = new Pageable();
    pageable.pageSize = 50;
    let searchName = '';
    if (term) {
      searchName = ",screenName:*" + term + "*";
      this.isQueryBased = true;
    } else {
      this.isQueryBased = false
    }

    this.elements = new InfiniteScrollableDataSource(this.elementService, "workspaceVersionId:"+this.option.version.id+searchName, undefined, 50);
    this.setNewElement(term);
  }

  setNewElement(term?:string) {
    if(!this.elements.isFetching){
      this.isNew = this.checkElementIsMatchedOrNot(term)? true : false;
      this.putCurrentElementOnTop();
    } else if(this.elements.isFetching) {
      setTimeout(()=> this.setNewElement(term), 200)
    }
  }
  putCurrentElementOnTop(){
    if(!this.elements.isEmpty){
      const reOrderedElements:Element[] = [];
      this.elements?.cachedItems?.forEach((element :Element ,ind)=>{
        if( this.option?.currentStepElementName === element?.name ){
          reOrderedElements.unshift(element);
        }else{
          reOrderedElements.push(element);
        }
      });
      this.elements.cachedItems = reOrderedElements;
    }
  }
  checkElementIsMatchedOrNot(term?:string) {
      let isNotMatched = false
    if(this.elements.isEmpty) {
      isNotMatched = true;
    } else if(term){
      isNotMatched = true;
      this.elements.cachedItems.forEach((element: Element) => {
        if (element.name.toLowerCase() == term.toLowerCase())
          isNotMatched = false;
      })
    }
    return isNotMatched;
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
            if (this.elementName) {
              this.searchText = this.elementName;
              this.fetchElement(this.searchText);
            } else {
              this.fetchElement();
            }
          })
        )
        .subscribe();
      this.focusInput();
    } else {
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
    }
  }

  attachScreenDebounceEvent() {
    if (this.searchScreenInput && this.searchScreenInput.nativeElement) {
      fromEvent(this.searchScreenInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(200),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (["ArrowDown", "ArrowUp"].includes(event.key))
              return;
            if (this.elementScreenName) {
              this.searchText = this.elementScreenName;
              this.fetchElementByScreenName(this.searchText);
            } else {
              this.fetchElement();
            }
          })
        )
        .subscribe();
      this.focusInput();
    } else {
      setTimeout(() => {
        this.attachScreenDebounceEvent();
      }, 100);
    }
  }

  get elementName() {
    return this.searchInput?.nativeElement?.textContent;
  }

  private focusInput() {
    if(this.searchInput.nativeElement)
      setTimeout(()=> {
        this.searchInput.nativeElement.focus();
      }, 100);
    else
      setTimeout(()=> {
        this.focusInput();
      }, 100);
  }

  selectElement(){
    let selectedName = this.elements?.cachedItems?.[this.currentFocusedIndex]
    if(selectedName)
      this.elementSuggestion ? this.mobileRecorderEventService.returnData.next({type: 'element', data: selectedName['name']}) : this.dialogRef.close(selectedName['name']);
    else
      this.openElementForm(this.elementName);
  }

  get elementScreenName() {
    return this.searchScreenInput?.nativeElement?.textContent;
  }

  selectScreenElement(){
    let selectedName = this.elements?.cachedItems?.[this.currentFocusedIndex]
    if(selectedName)
      this.elementSuggestion ? this.mobileRecorderEventService.returnData.next({type: 'element', data: selectedName['name']}) : this.dialogRef.close(selectedName['name']);
    else
      this.openElementForm(this.elementName);
  }

  openElementForm(name?: string, elementId?: string) {
    let sendDetails = {
      versionId: this.option.version.id,
      elementId: elementId,
      name:name,
      isNew: true,
      testCaseId: this.option?.testCase?.id,
      isDryRun: this.option?.isDryRun,
      testCaseResultId: this.option.testCaseResultId,
      isStepRecordView: this.option.isStepRecordView
    }
    if (this.option.isStepRecordView) {
      this.mobileRecorderEventService.suggestionContent.next(Object.assign(sendDetails, {
        content: this.mobileRecorderEventService.editElementPopup
      }));
      return
    }
    this.elementForm = this.matModal.open(ElementFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      data: sendDetails,
      panelClass: ['mat-dialog', 'rds-none'],
      ...this.alterStyleIfStepRecorder()
    });
    let afterClose = (name) => {
      if(name){
        this.dialogRef.close(name);
      }
    }
    this.elementForm.afterClosed().subscribe(element=> afterClose(element));
  }

  scrollUpElementFocus() {
    if (this.currentFocusedIndex)
      --this.currentFocusedIndex;
    let target = <HTMLElement>document.querySelector(".h-active");
    if(target)
    target.parentElement.scrollTop = target.offsetTop - target.parentElement.offsetTop;
  }

  scrollDownElementFocus() {
    if (this.currentFocusedIndex >= this.elements.totalElements)
      return;
    ++this.currentFocusedIndex;
    console.log(this.currentFocusedIndex, this.elements.totalElements);
    let target = <HTMLElement>document.querySelector(".h-active");
    if(target)
    target.parentElement.scrollTop = target?.offsetTop - target?.parentElement?.offsetTop;
  }

  alterStyleIfStepRecorder() {
    if (!Boolean(this.option.isStepRecordView)) return {};
    let mobileStepRecorderComponent: MobileStepRecorderComponent = this.matModal.openDialogs.find(dialog => dialog.componentInstance instanceof MobileStepRecorderComponent).componentInstance;
    let clients = {
      height: mobileStepRecorderComponent.customDialogContainerH50.nativeElement.clientHeight + 'px',
      width: mobileStepRecorderComponent.customDialogContainerH50.nativeElement.clientWidth + 'px',
      position: {
        top: mobileStepRecorderComponent.customDialogContainerH50.nativeElement.getBoundingClientRect().top + 'px',
        left: mobileStepRecorderComponent.customDialogContainerH50.nativeElement.getBoundingClientRect().left + 'px'
      },
      hasBackdrop: false,
      panelClass: ['modal-shadow-none', 'px-10']
    }
    return clients;
  }

  get isWeb(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.WebApplication;
  }

  get isMobileWeb(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.MobileWeb;
  }

  get isAndroidNative(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.AndroidNative;
  }

  get isIosNative(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.IOSNative;
  }

  openAddEditElement() {
    const dialogRef = this.matModal.open(ElementFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      data: {
        versionId: this.option.version.id,
        isStepRecordView: this.option.isStepRecordView
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close(res);
        }
      });
  }

  closeSuggestion() {
    this.elementSuggestion ? this.mobileRecorderEventService.setEmptyAction() : this.dialogRef.close();
  }
}
