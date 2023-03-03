import {Component, ElementRef, Inject, Input, OnInit, Optional, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {TestDataService} from "../../services/test-data.service";
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";
import {TestDataType} from "../../enums/test-data-type.enum";
import {TestCaseService} from "../../services/test-case.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {Page} from "../../shared/models/page";
import {TestData} from "../../models/test-data.model";
import {TestCase} from "../../models/test-case.model";

@Component({
  selector: 'app-action-test-data-parameter-suggestion',
  templateUrl: './action-test-data-parameter-suggestion.component.html',
  styles: []
})
export class ActionTestDataParameterSuggestionComponent  extends BaseComponent implements OnInit  {

  @Optional() @Input('testDataProfileDetails') testDataProfileDetails;
  @Optional() @Input('mobileDataProfileDetails') mobileDataProfileDetails;
  public dataProfileSuggestion: any[];
  public filteredSuggestion: any[];
  public searchText: string;
  public testDataName: String = "";
  public currentFocusedIndex: number = 0;
  public showVideo: Boolean = false;
  public isQueryBased: boolean = false;
  public versionId: number;
  public testCaseId: number;
  public testcase: TestCase;
  public multiDataProfileSuggestions: any[];
  public filteredMultiDataProfileSuggestions:any[];
  public showRefreshOption: boolean=false;
  public filteredTdpDatas:Page<TestData>=new Page<TestData>();
  public selectedTdpId:number;
  public selectedTestData:TestData;
  public isQueryBasedTdp:boolean=false;
  @ViewChild('searchInput') searchInput: ElementRef;


  constructor(
    private dialogRef: MatDialogRef<ActionTestDataParameterSuggestionComponent, any>,
    private testDataService: TestDataService,
    public testCaseService: TestCaseService,
    @Inject(MAT_DIALOG_DATA) public option: { dataProfileId?: number, dataProfileIds?:any[], versionId: number, testCaseId: number, stepRecorderView: boolean ,testCase: TestCase },
    private mobileRecorderEventService: MobileRecorderEventService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService
  ) {
    super(authGuard, notificationsService, translate);
    this.versionId = this.option.versionId;
    this.testCaseId = this.option.testCaseId;
    this.testcase = this.option.testCase;
  }

  ngOnInit(): void {
    if (this.testDataProfileDetails) {
      this.option = this.testDataProfileDetails;
      this.versionId = this.option.versionId;
      this.testCaseId = this.option.testCaseId;
      this.testcase = this.option.testCase;
    }
    if (this.mobileDataProfileDetails) {
      this.option = this.mobileDataProfileDetails;
      this.versionId = this.option.versionId;
      this.testCaseId = this.option.testCaseId;
      this.testDataProfileDetails = this.mobileDataProfileDetails;
    }
    this.fetchDataParameter();
    this.attachDebounceEvent();
  }

  fetchDataParameter() {
   /* if( this.option.dataProfileIds?.length ){
      this.multiDataProfileSuggestions = [];
      let allTDPIds = this.option.dataProfileIds.map((dataProfile)=>{return dataProfile.testDataId});
      this.testDataService.findAll("id@"+allTDPIds.join("#")).subscribe(res=>{
        res.content.forEach((testData)=>{
          let dataProfile = this.option.dataProfileIds.find((data)=> {
            return data.testDataId === testData.id
          });
          Object.keys(testData.data[0].data).forEach((key)=>{
            this.multiDataProfileSuggestions.push({...dataProfile,tdpName:testData.name,key});
          })
          this.onSearchTdp();
          this.filter();
        });
      });

    }
    else if(this.option.dataProfileId) {
      this.testDataService.show(this.option.dataProfileId).subscribe(res => {
        this.dataProfileSuggestion = [];
        this.testDataName = res.name;
        res.data.forEach((dataSet: TestDataSet) => {
          for (let key in dataSet.data) {
            this.dataProfileSuggestion.push(key)
          }
        })
        this.dataProfileSuggestion = [...new Set(this.dataProfileSuggestion)];
        this.onSearchTdp();
        this.filter()
      })
    }*/

    if( this.option.dataProfileIds?.length ){
      let allTdpIds = this.option.dataProfileIds.map((tdpData)=> tdpData.tdpId);
      this.testDataService.findAll(`id@${allTdpIds.join("#")}`).subscribe(res=>{
        let contentOrder = [];
        console.log("this.option.tdpDatas",this.option.dataProfileIds);
        this.option.dataProfileIds.forEach((dataProfile)=>{
          let testData = res.content.find((data)=>{
            return dataProfile.tdpId === data.id;
          });
          let newTestData = Object.assign({},testData);
          newTestData.testDataProfileStepId = dataProfile.id;
          newTestData.name=`${newTestData.name}${dataProfile.stepDisplayNumber?` [#${dataProfile.stepDisplayNumber}]`:''}`;
          newTestData.parameters = Object.keys(newTestData.data[0].data);
          contentOrder.push(newTestData);
        });
        this.testDataProfileDetails=res;
        this.testDataProfileDetails.content=contentOrder;
        this.selectedTdpId = this.testDataProfileDetails.content[0].id;
        this.selectedTestData = this.testDataProfileDetails.content[0];
        if(this.testDataProfileDetails.content[0].isMigrated){
          this.populateDataProfileSuggestion();
        }
        else{
          this.dataProfileSuggestion = Object.keys(this.testDataProfileDetails.content[0].data[0].data);
          this.onSearchTdp();
          this.filter()
        }
      });
    }

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
              this.filteredMultiDataProfileSuggestions = this.multiDataProfileSuggestions;
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
      this.filteredMultiDataProfileSuggestions = [];
      if(this.dataProfileSuggestion?.length){
        this.dataProfileSuggestion.forEach(suggestion => {
          if (suggestion.toLowerCase().includes(searchText.toLowerCase())) {
            this.filteredSuggestion.push(suggestion);
          }
        })
        this.filteredSuggestion = [...new Set(this.filteredSuggestion)];
      }
      else if(this.multiDataProfileSuggestions?.length){
        this.multiDataProfileSuggestions.forEach((suggestion)=>{
          if(suggestion?.key?.toLowerCase().includes(searchText.toLowerCase())){
            this.filteredMultiDataProfileSuggestions.push(suggestion);
          }
        })
      }
    } else if (!searchText) {
      this.filteredSuggestion = this.dataProfileSuggestion;
      this.filteredMultiDataProfileSuggestions = this.multiDataProfileSuggestions;
    }
  }


  selectedSuggestion(suggestion?: any, testDataProfileStepId?:number) {
    suggestion = suggestion || this.filteredSuggestion[this.currentFocusedIndex];
    testDataProfileStepId = testDataProfileStepId || this.selectedTestData?.testDataProfileStepId;
    this.mobileDataProfileDetails ? this.mobileRecorderEventService.returnData.next({type: TestDataType.parameter, data:{ suggestion , testDataProfileStepId } }) : this.dialogRef.close({suggestion,testDataProfileStepId});
  }

  selectSuggestion(suggestion?: any,id?:number) {
    suggestion = suggestion || this.filteredMultiDataProfileSuggestions[this.currentFocusedIndex].key;
    id = id || this.selectedTestData?.testDataProfileStepId;
    this.mobileDataProfileDetails ? this.mobileRecorderEventService.returnData.next({type: TestDataType.parameter, data: suggestion}) : this.dialogRef.close({suggestion,testDataProfileStepId:id});
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

  fetchTestCase(){
    this.testCaseService.show(this.option.testCaseId).subscribe(res => {
      if(res.testDataId){
        this.option.dataProfileIds = [{ tdpId : res.testDataId }];
        this.fetchDataParameter();
        this.testCaseService.refeshTestCaseAfterSaveOrUpdate.next(true);
      }
      else{
        this.showNotification( NotificationType.Alert , this.translate.instant("test_data_profile_suggestion.not_found.start_title") );
      }
    });
  }

  closeSuggestion(data?) {
    this.mobileDataProfileDetails ? this.mobileRecorderEventService.setEmptyAction() : data ? this.showRefreshOption = true : this.dialogRef.close(data);
  }

  refreshTestDataProfileId(){
    this.fetchTestCase()
  }

  filterTdp(term:string){
    let filteredContent = this.testDataProfileDetails.content.filter((testData)=>{
      return testData.name.toLowerCase().includes(term.toLowerCase());
    });
    this.filteredTdpDatas.content = filteredContent;
  }

  onSearchTdp(term?:string) {
    this.filteredTdpDatas = new Page<TestData>();
    if(term){
      this.isQueryBasedTdp=true;
      this.filterTdp(term);
    }
    else{
      this.isQueryBasedTdp=false;
      this.filteredTdpDatas.content = this.testDataProfileDetails?.content;
    }
  }

  onSelectTdp(selectedTestData?){
    if( selectedTestData ){
      this.selectedTestData = selectedTestData;
      this.selectedTdpId = selectedTestData.id;
      if(selectedTestData.isMigrated){
        this.populateDataProfileSuggestion();
      }else {
        this.dataProfileSuggestion = selectedTestData.parameters;
        this.filter();
      }
    }
  }
  populateDataProfileSuggestion(){
    this.testDataService.findAll("id:"+ this.selectedTdpId).subscribe(res=>{
      this.dataProfileSuggestion = Object.keys(res.content[0].data[0].data);
      this.onSearchTdp();
      this.filter()
    })
  }
}
