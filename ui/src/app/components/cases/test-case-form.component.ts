import {Component, OnInit} from '@angular/core';
import {TestCase} from "../../models/test-case.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {TestCaseService} from "../../services/test-case.service";
import {Page} from "../../shared/models/page";
import {TestCaseStatus} from "../../enums/test-case-status.enum";
import {TestCasePriority} from "../../models/test-case-priority.model";
import {TestCasePrioritiesService} from "../../services/test-case-priorities.service";
import {TestCaseTypesService} from "../../services/test-case-types.service";
import {TestCaseTagService} from "../../services/test-case-tag.service";
import {TestCaseType} from "../../models/test-case-type.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestData} from "../../models/test-data.model";
import {TestDataService} from "../../services/test-data.service";
import {TestDataSet} from "../../models/test-data-set.model";
import {UserPreferenceService} from "../../services/user-preference.service";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestStepForLoop} from "../../models/test-step-for-loop.model";
import {MatDialog} from "@angular/material/dialog";
import {WarningModalComponent} from "../../shared/components/webcomponents/warning-modal.component";
import {BaseComponent} from "../../shared/components/base.component";
import {PromptModalComponent} from "../../shared/components/webcomponents/prompt-modal.component";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {ToastrService} from "ngx-toastr";
import {list} from "serializr";
import {TestCasePrerequisiteChangeComponent} from "../webcomponents/test-case-prerequisite-change.component";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestSuiteService} from "../../services/test-suite.service";

@Component({
  selector: 'app-test-case-form',
  templateUrl: './test-case-form.component.html',
  styles: []
})
export class TestCaseFormComponent extends BaseComponent implements OnInit {
  public testCase: TestCase;
  public testCaseForm: FormGroup;
  public formSubmitted: Boolean = false;
  public testCaseId: number;
  public isStepGroup: Boolean = false;
  public testCasePrioritiesList: Page<TestCasePriority>;
  public testCaseTypeList: Page<TestCaseType>;
  public testDataList: Page<TestData>;
  public testCaseList: Page<TestCase>;
  public testDataSetList: TestDataSet[];
  public versionId: number;
  public showDetails: Boolean = false;
  public version: WorkspaceVersion;
  public saving = false;
  public testCaseStatus = TestCaseStatus;
  public isTestManager: boolean;
  public isRunning: boolean;
  public startArray: Array<{setName: String,index: number}> = [];
  public endArray: Array<{setName: String,index: number}> = [];
  private originalTestDataId: number = 0;
  private testDataCanBeChanged: boolean = false;
  public associatedParametersPopupOpen: boolean = false;
  private oldPreRequisite: number;
  private confirmedStatusChange: boolean = false;
  private linkedTestSuites: InfiniteScrollableDataSource;
  private oldStatus: TestCaseStatus;

  constructor(
    private matModal: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService:ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private testPlanResultService: TestPlanResultService,
    private  testCaseService: TestCaseService,
    private  testCaseResultService: TestCaseResultService,
    private testCasePrioritiesService: TestCasePrioritiesService,
    private testCaseTypesService: TestCaseTypesService,
    public tagService: TestCaseTagService,
    private workspaceVersionService: WorkspaceVersionService,
    private testDataService: TestDataService,
    private testSuiteService: TestSuiteService,
    public userPreferenceService: UserPreferenceService,
    private dialog: MatDialog,
  ) {
    super(authGuard, notificationsService, translate,toastrService)
  }

  get statuses() {
    return Object.keys(TestCaseStatus);
  }

  get isStepGroupUrl() {
    return this.router.url.indexOf("/step_groups") > -1;
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      this.versionId = this.route.parent.parent.snapshot.params.versionId;
      this.testCaseId = params.testCaseId;
      this.route.queryParams.subscribe((queryParams: Params) => {
        if (queryParams.isGroup)
          this.isStepGroup = Boolean(JSON.parse(queryParams.isGroup));
        if (this.versionId > 0) {
          this.pushToParent(this.route, {...params, ...{versionId: this.versionId, isGroup: queryParams.isGroup}});
        }
      });
      if (this.testCaseId) {
        this.fetchTestCase()
      } else if (!this.testCaseId) {
        this.fetchVersion();
        this.createTestCase();
      }
    });
    this.isTestManager = true;
  }

  createTestCase() {
    this.testCase = new TestCase();
    this.testCase.status = TestCaseStatus.READY;
    this.testCase.isStepGroup = this.isStepGroupUrl;
    this.testCase.testDataStartIndex = 0;
    this.testCase.deleted = false;
  }

  public noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }

  addValidations() {
    this.testCaseForm = new FormGroup({
      name: new FormControl(this.testCase.name, [
        Validators.required, Validators.maxLength(125) , Validators.minLength(4),this.noWhitespaceValidator
      ]),
      description: new FormControl(this.testCase.description, []),
      priority: new FormControl(this.testCase.priorityId, []),
      type: new FormControl(this.testCase.type, []),
      preRequisite: new FormControl(this.testCase.preRequisite, []),
      testData: new FormControl(this.testCase.testDataId, []),
      startIndex: new FormControl(this.testCase.isDataDriven && this.testCase.testDataStartIndex > 0 ? this.testCase.testDataStartIndex : -1),
      endIndex: new FormControl(this.testCase.isDataDriven && this.testCase.testDataEndIndex > 0 ? this.testCase.testDataEndIndex : -1)
    });
  }

  fetchVersion() {
    this.workspaceVersionService.show(this.versionId).subscribe(res => {
      this.version = res;
      this.addValidations();
      if (!this.isStepGroupUrl) {
        this.fetchTestCaseTypes();
        this.fetchTestCasePriorities();
        this.fetchTestDataProfile();
        this.fetchTestCases();
      }
    });
  }

  fetchTestCase() {
    this.testCaseService.show(this.testCaseId).subscribe(res => {
      this.showDependency(res);
      this.testCase = res;
      if(this.originalTestDataId == 0)
        this.originalTestDataId = res.testDataId;
      this.oldPreRequisite = res.preRequisite;
      this.isStepGroup = this.testCase.isStepGroup;
      this.versionId = this.testCase.workspaceVersionId;
      this.pushToParent(this.route, {...this.route.snapshot.params, ...{versionId: this.versionId}});
      this.fetchVersion();
      this.setIsRunning();
    });
  }

  setIsRunning(){
    this.testCaseResultService.findAll("testCaseId:"+this.testCaseId+",result:"+ResultConstant.QUEUED).subscribe(res => {
      this.isRunning = (this.isRunning) ? this.isRunning : res.content.length > 0;
    })
  }

  showDependency(testcase: TestCase) {
    this.testCasePrioritiesService.show(testcase.priorityId).subscribe(res => {
      this.testCase.testCasePriority = res;
    })
    this.testCaseTypesService.show(testcase.type).subscribe(res => {
      this.testCase.testCaseType = res;
    })
    if (testcase.preRequisite)
      this.testCaseService.show(testcase.preRequisite).subscribe(res => {
        this.testCase.preRequisiteCase = res
      })
    if (testcase.testDataId)
      this.testDataService.show(testcase.testDataId).subscribe(res => {
        this.testCase.testData = res;
      })
  }

  saveTestCase() {
    this.testCase.description = this.testCaseForm.controls.description.value;
    this.formSubmitted = true;
    if (this.testCaseForm.valid) {
      this.saving = true;
      if (this.testCase.testDataId == 0) {
        delete this.testCase.testDataId;
      }
      this.testCase.workspaceVersionId = this.versionId;
      let fieldName = this.isStepGroupUrl ? 'Step Group' : 'Test Case';
      this.testCaseService.create(this.testCase).subscribe((testcase) => {
          this.saving = false;
          this.translate.get('message.common.created.success', {FieldName: fieldName}).subscribe((res) => {
            this.showNotification(NotificationType.Success, res);
            this.router.navigate(['/td', 'cases', testcase.id, 'steps'])
          })
        },
        error => {
          this.saving = false;
          this.translate.get('message.common.created.failure', {FieldName: fieldName}).subscribe((res) => {
            this.showAPIError(error, res,'Test Case or Step Group');
          })
        })
    }
  }

  updateTestCase(list?:InfiniteScrollableDataSource) {
    this.testCase.description = this.testCaseForm.controls.description.value;
    this.formSubmitted = true;
    if (this.testCaseForm.invalid) {
      this.showDetails = true;
      return false;
    }
    if (this.testCaseForm.valid) {
      this.saving = true;
      if (this.testCase.testDataId == 0) {
        delete this.testCase.testDataId;
      }
      let fieldName = this.isStepGroupUrl ? 'Step Group' : 'Test Case';
     if(this.preRequisiteChanged()) {
        this.fetchLinkedSuites();
        this.setPreRequisiteAffectsTestSuites(fieldName)
      } else
        this.updateAfterValidation(fieldName, list);
    }
  }

  private updateAfterValidation(fieldName, list?:any) {
    this.testCaseService.update(this.testCase).subscribe(
      (testcase) => {
        this.saving = false;
        if(this.oldPreRequisite != this.testCase.preRequisite && Boolean(list)){
          let msg =
            this.translate.instant('test_case.prerequisite.update.success', {CaseName: this.testCase.name,
              suites:(list.cachedItems.map(execution=>execution.name).join(", ") )+ (list.cachedItems.length==list.totalElements?"":", ...")});
          this.showNotification(NotificationType.Success, msg);
        } else {
          this.translate.get('message.common.update.success', {FieldName: fieldName}).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        }
        this.router.navigate(['/td', 'cases', testcase.id])
      },
      error => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: fieldName}).subscribe((res) => {
          this.showAPIError(error, res, fieldName)
        })
      });
  }

  toggleDataProfile(testData?) {
    this.startArray = []
    this.endArray = []
    if (testData || testData == null) {
      if (!testData) {
        this.testCase.testDataId = 0;
        this.testCase.isDataDriven = false;
        return;
      }
    }
    let dataProfile = this.testDataList.content[0]
    this.testDataList.content.forEach(data => {
      if (data.id == testData.id) {
        dataProfile = data;
      }
    });
    this.testCase.testDataId = dataProfile.id;
    this.testCase.testData = dataProfile;
    this.setStartValue(dataProfile, this.testCase.testDataStartIndex, this.testCase.testDataEndIndex );
  }

  setStartValue(testData: TestData, startIndex?, endIndex?) {
    if (testData && testData.data) {
      this.testDataSetList = testData.data;
    }
    if(!this.testCase.isDataDriven) {
      this.testCase.testDataStartIndex = 0;
    }
    testData.data.forEach((data, i) => this.startArray.push({setName: data.name, index: i}))
    let loopDetails = new TestStepForLoop();
    loopDetails.startIndex = (startIndex!= null && startIndex >= 0 && (startIndex <= this.startArray.length))? startIndex :
      (this.testCase.isDataDriven? -1 : 0);
    loopDetails.endIndex = (endIndex > 0 && (endIndex <= this.startArray.length)) ? endIndex: -1;
    this.testCase.testDataStartIndex = loopDetails.startIndex;
    this.testCase.testDataEndIndex = loopDetails.endIndex;
    this.toggleStartIndex(endIndex);
  }

  fetchTestDataProfile(term?) {
    let searchName = '';
    if (term) {
      searchName = ",testDataName:*" + term + "*";
    }
    this.testDataService.findAll("versionId:" + this.version.id + searchName).subscribe(res => {
      this.testDataList = res;
      if(this.testCase?.id && this.testCase?.testDataId)  {
        if(this.testDataList?.content && !this.testDataList?.content?.find(req => req.id == this.testCase.testDataId)) {
          this.testDataList.content.push(this.testCase.testData)
        }
      }
    });
  }

  fetchTestCasePriorities(term?) {
    let searchName = 'workspaceId:'+this.version.workspaceId;
    if (term) {
      searchName = ",name:*" + term + "*";
    }
    this.testCasePrioritiesService.findAll(searchName).subscribe(res => {
      this.testCasePrioritiesList = res;
      if (this.testCasePrioritiesList?.content?.length) {
        if (!this.testCase.priorityId) {
          this.testCase.priorityId = this.testCasePrioritiesList.content[0].id;
        } else if(this.testCase?.id && this.testCase?.priorityId)  {
          if(!this.testCasePrioritiesList?.content?.find(req => req.id == this.testCase.priorityId)) {
            this.testCasePrioritiesList.content.push(this.testCase.testCasePriority)
          }
        }
      }
    });
  }

  fetchTestCaseTypes(term?) {
    let searchName = 'workspaceId:'+this.version.workspaceId;
    if (term) {
      searchName = ",name:*" + term + "*";
    }
    this.testCaseTypesService.findAll(searchName).subscribe(res => {
      this.testCaseTypeList = res;
      if (this.testCaseTypeList?.content?.length) {
        if (!this.testCase?.type) {
          this.testCase.type = this.testCaseTypeList.content[0].id
        } else if(this.testCase?.id && this.testCase?.type)  {
          if(!this.testCaseTypeList?.content?.find(req => req.id == this.testCase.type)) {
            this.testCaseTypeList.content.push(this.testCase.testCaseType)
          }
        }
      }
    });
  }

  getCurrentItem(items: Page<any>, id: number) {
    let selectedItems = null;
    items.content.filter(item => {
      if (item.id == id) {
        selectedItems = item;
      }
    })
    return selectedItems;
  }

  fetchTestCases(term?) {
    let searchName = '';
    if (term) {
      searchName = ",name:*" + term + "*";
    }
    this.testCaseService.findAll("isStepGroup:false,workspaceVersionId:" + this.versionId + ",deleted:false" + searchName + ",status:" + TestCaseStatus.READY, undefined).subscribe(res => {
      res.content = res.content.filter(testcase => this.testCaseId != testcase.id)
      if(this.testCase?.id && this.testCase?.preRequisite)  {
        if(!res?.content?.find(req => req.id == this.testCase.preRequisite)) {
          res.content.push(this.testCase.preRequisiteCase)
        }
      }
      this.testCaseList = res;
    })
  }

  setPreRequisite(testcase) {
    this.testCase.preRequisite = null;
    if (testcase) {
      this.testCase.preRequisite = testcase?.id;
    }
  }

  setType(testCaseType) {
    this.testCase.type = testCaseType?.id;
  }

  setPriorityId(testCasePriorities) {
    this.testCase.testCasePriority = testCasePriorities;
    this.testCase.priorityId = testCasePriorities.id;
  }

  goBack() {
    if (this.testCase.id) {
      this.router.navigate(['/td', 'cases', this.testCase.id])
    } else {
      this.router.navigate(['/td', this.versionId, this.isStepGroupUrl ? 'step_groups' : 'cases'])
    }
  }

  setTags(tags: any) {
    this.testCase.tags = [];
    if (tags)
      this.testCase.tags = tags;
  }

  toggleStartIndex(endIndex?) {
    let startIndex: number = this.testCase.testDataStartIndex>0 ? parseInt(String(this.testCase.testDataStartIndex)) : 1;
    let startArray = [...this.startArray]
    this.endArray = startArray.splice(startIndex , startArray.length);
    if(this.testCase.testDataStartIndex > this.testCase.testDataEndIndex || endIndex!=undefined) {
      this.testCase.testDataEndIndex = (endIndex > 0 && (endIndex <= this.startArray.length)) ? endIndex : -1;
      this.testCaseForm.patchValue({
        endIndex: this.testCase.testDataEndIndex
      })
    }
  }

  setTestDataStartIndex() {
    this.testCase.testDataStartIndex = -1;
    this.testCase.testDataEndIndex = -1;
    this.testCase.testDataStartIndex = this.testCase.isDataDriven?
      this.testCase.testDataStartIndex.valueOf() ==  0 ?
        this.testCase.testDataStartIndex.valueOf()-1 : this.testCase.testDataStartIndex.valueOf()
      :
      this.testCase.testDataStartIndex.valueOf() == -1 ?
        this.testCase.testDataStartIndex.valueOf()+1 : this.testCase.testDataStartIndex.valueOf();
  }

  private associatedParametersPopup(testData?) {
    this.associatedParametersPopupOpen = true;
    const dialogRef = this.dialog.open(WarningModalComponent, {
      width: '450px',
      data: {
        title:this.translate.instant("message.common.confirmation.default_change"),
        message: this.translate.instant("test_data.confirmation.change_note"),
        confirmMessage:true
      },
      panelClass: ['matDialog', 'delete-confirm']
    });
    dialogRef.afterClosed().subscribe(result => {
      this.associatedParametersPopupOpen = false;
      if (result) {
        this.testDataCanBeChanged = true;
        this.toggleDataProfile(testData);
      } else {
        this.testDataCanBeChanged = false;
      }
    });
  }

  checkForAssociatedParameters(testData?) {
    if (this.originalTestDataId!=null && this.originalTestDataId!=0 && testData?.id!=this.originalTestDataId
      && !this.testDataCanBeChanged && !this.associatedParametersPopupOpen) {
      this.associatedParametersPopup(testData);
    } else {
      this.toggleDataProfile(testData);
    }
  }

  private preRequisiteChanged(): boolean {
    return !this.confirmedStatusChange && this.oldPreRequisite != this.testCase.preRequisite;
  }

  private fetchLinkedSuites() {
    let query = "appVersionId:" + this.versionId + "&testcaseId:" + this.testCaseId;
    this.linkedTestSuites = new InfiniteScrollableDataSource(this.testSuiteService, query, "name");
  }

  private setPreRequisiteAffectsTestSuites(fieldName) {
    setTimeout(() => {
      if (this.linkedTestSuites.isFetching)
        this.setPreRequisiteAffectsTestSuites(fieldName);
      else {
        if (this.linkedTestSuites.isEmpty)
          this.updateAfterValidation(fieldName);
        else {
          this.saving = false;
          this.openPreRequisiteChangeConfirmBox(this.linkedTestSuites);
        }
      }
    }, 200);
  }

  private openPreRequisiteChangeConfirmBox(list: InfiniteScrollableDataSource) {
    let description = this.translate.instant('testcase.prerequisite_linked_with_plans', {status: this.translate.instant('testcase.status_' + this.testCase.status)});
    const dialogRef = this.dialog.open(TestCasePrerequisiteChangeComponent, {
      width: '468px',
      height: 'auto',
      data: {
        description: description,
        testSuites: list,
        testCaseId: this.testCaseId
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed()
      .subscribe(result => {
        if (result) {
          this.oldPreRequisite = this.testCase.preRequisite;
          this.updateTestCase(list);
        }
      });
  }
}
