import {Component, OnInit} from '@angular/core';
import {TestCaseService} from "../../services/test-case.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {BaseComponent} from "../../shared/components/base.component";
import {TestCase} from "../../models/test-case.model";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog} from "@angular/material/dialog";
import {TestCaseSummaryComponent} from "../webcomponents/test-case-summary.component";
import {TestCaseCloneFormComponent} from "../webcomponents/test-case-clone-form.component";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestSuiteService} from "../../services/test-suite.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {UserPreferenceService} from "../../services/user-preference.service";
import {UserPreference} from "../../models/user-preference.model";
import {DryRunFormComponent} from '../webcomponents/dry-run-form.component';
import {TestStep} from '../../models/test-step.model';
import {ChromeRecorderService} from "../../services/chrome-recoder.service";
import {ToastrService} from "ngx-toastr";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";
import {EntityType} from "../../enums/entity-type.enum";
import {XrayKeyWarningComponent} from "../../agents/components/webcomponents/xray-key-warning-component";
import {StepGroupFilterService} from "../../services/step-group-filter.service"

@Component({
  selector: 'app-test-case-details',
  templateUrl: './test-case-details.component.html',
  styles: [`.test-case-details{
    display: flex;
    flex-direction: column;}
  .theme-details-scroll{
    height: calc(100vh - 8.5rem);
  }`]
})
export class TestCaseDetailsComponent extends BaseComponent implements OnInit {

  public testCaseId: number;
  public testCase: TestCase;
  public isSearchEnable: boolean = false;
  public isTestCaseFetchingCompleted: boolean = false;
  public version: WorkspaceVersion;
  private userPreference: UserPreference;
  public selectedStepsList: TestStep[];
  public stepsLength: number;
  public entityType: EntityType = EntityType.TEST_CASE;
  public entityExternalMapping: EntityExternalMapping;
  public enabledStepPresent: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private testPlanResultService: TestPlanResultService,
    // private userService: UserService,
    private testCaseService: TestCaseService,
    private testSuiteService: TestSuiteService,
    private versionService: WorkspaceVersionService,
    private matModal: MatDialog,
    private router: Router,
    private userPreferenceService: UserPreferenceService,
    private chromeRecorderService : ChromeRecorderService,
    public entityExternalMappingService : EntityExternalMappingService,
    public stepGroupFilterService: StepGroupFilterService,
  ) {
    super(authGuard, notificationsService, translate,toastrService);
  }

  get testCaseName() {
    return this.testCase?.isStepGroup ? 'Step Group' : 'Test Case';
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.testCaseId = params.testCaseId;
      this.entityType = EntityType.TEST_CASE;
      this.fetchUserPreference()
      this.fetchTestCase();
    });
    this.testCaseService.getStepLengthEmitter().subscribe(hasInspectorFeatureres => {
      this.stepsLength = hasInspectorFeatureres;
    })
    this.testCaseService.enableStepdata$.subscribe(enableStepdata => this.enabledStepPresent = enableStepdata);

  }

  fetchUserPreference() {
    this.userPreferenceService.show().subscribe(res => this.userPreference = res)
  }

  hasInspectorFeature() {
    return (
      this.version && this.version.workspace.isAndroidNative
    ) || (this.version && this.version.workspace.isIosNative);
  }

  fetchTestCase() {
    this.testCaseService.show(this.testCaseId).subscribe(res => {
      this.fetchVersion(res.workspaceVersionId);
      this.testCase = res;
      // this.fetchUsers();
      this.isTestCaseFetchingCompleted = true;
    }, error =>{
      this.isTestCaseFetchingCompleted = true;
    })
  }

  fetchVersion(versionId) {
    this.versionService.show(versionId).subscribe(res => {
      this.version = res;
      if (this.version.workspace.isWebMobile) {
        this.chromeRecorderService.isChromeBrowser();
        this.chromeRecorderService.pingRecorder();
        setTimeout(() => {
          if (this.chromeRecorderService.isInstalled) {
            this.chromeRecorderService.recorderVersion = this.version;
            this.chromeRecorderService.recorderTestCase = this.testCase;
          }
        }, 200);
      }
    });
  }

  deleteTestCase(permanently?) {
    this.translate.get("message.common.confirmation.message", {FieldName: this.testCaseName }).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: this.translate.instant("message.common.confirmation.message", {FieldName: this.testCaseName }),
          isPermanentDelete: permanently,
          title: this.testCaseName,
          item: this.testCaseName.toLowerCase(),
          name: this.testCase.name,
          note: this.translate.instant('message.common.confirmation.test_data_des', {Item:this.testCaseName.toLowerCase()}),
          confirmation: permanently ? this.translate.instant("message.common.confirmation.note") : this.translate.instant("message.common.confirmation.note_trash"),
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          if (permanently)
            this.deletePermanently();
          else
            this.markAsDeleted()
        }
      });
    })
  }

  markAsDeleted() {
    this.testCase.isStepGroup ? this.markGroupAsDeleted():this.markCaseAsDeleted();
  }

  markGroupAsDeleted(){
    let ids: number[] = [this.testCaseId];
    this.testCaseService.bulkMarkAsDeleted(ids).subscribe({
        next: () => {
          this.deletionSuccessNotification();
        },
        error: (error) => {
          this.deletionFailureNotification(error);
        }
      }
    );
  }

  markCaseAsDeleted(){    this.testCaseService.markAsDeleted(this.testCaseId).subscribe({
        next: () => {
          this.deletionSuccessNotification();
        },
        error: (error) => {
          this.deletionFailureNotification(error);
        }
      }
    );
  }
  deletionSuccessNotification(){
    this.fetchTestCase();
    this.translate.get("message.common.deleted.success", {FieldName: this.testCaseName }).subscribe((res: string) => {
      this.showNotification(NotificationType.Success, res);
    });
  }

  deletionFailureNotification(error){
    if (error.status == "400") {
      this.showNotification(NotificationType.Error, error.error);
    } else {
      this.translate.get("message.common.deleted.failure", {FieldName: this.testCaseName }).subscribe((res: string) => {
        this.showNotification(NotificationType.Error, res);
      });
    }
  }

  openDetails() {
    this.matModal.open(TestCaseSummaryComponent, {
      width: '80%',
      data: {testCase: this.testCase},
      panelClass: ['mat-dialog', 'rds-none']
    })
  }

  openTestCaseClone() {
    let dialogRef = this.matModal.open(TestCaseCloneFormComponent, {
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        testCase: this.testCase
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if(res)
        this.router.navigate(['/td', 'cases',  res.id]);
    })
  }

  restore() {
    this.testCaseService.restore(this.testCaseId).subscribe({
        next: () => {
          this.fetchTestCase();
          this.translate.get("message.common.restore.success", {FieldName: this.testCaseName }).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          if (error.status == "400") {
            this.showNotification(NotificationType.Error, error.error);
          } else {
            this.translate.get("message.common.restore.failure", {FieldName: this.testCaseName }).subscribe((res: string) => {
              this.showAPIError(error, res, this.testCaseName);
            });
          }
        }
      }
    );
  }

  deletePermanently() {
    this.testCaseService.destroy(this.testCaseId).subscribe({ next : () => {
        let redirect: any[] = ['/td', this.testCase?.workspaceVersionId, this.testCase?.testcaseRedirection];
        if(this.testCase.isStepGroup && this.userPreference?.testCaseFilterId) {
          this.stepGroupFilterService.show(this.userPreference?.testCaseFilterId).subscribe(res =>{
              redirect = [...redirect, 'filter', this.userPreference?.testCaseFilterId]
              this.router.navigate(redirect);
            },
            err => {
              this.router.navigate(redirect);
            }
          )
        }
        else {
          redirect = [...redirect, 'filter', this.userPreference?.testCaseFilterId]
          this.router.navigate(redirect);
        }    },

    error: (error) => {
          this.showNotification(NotificationType.Error, error && error.error && error.error.error ? error.error.error :
          this.translate.instant("message.component.delete.failure"));
        }
    })
  }

  public fetchLinkedCases() {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testSuiteService, ",testcaseId:" + this.testCaseId);
    waitTillRequestResponds();
    let _this = this;
    /**
     * This is a function which is used to add a timeout when testCases.isFetching returns true. Gives a timeout for the testcases to be fetched.
     * @example
     *
     * waitTillRequestResponds()
     * */
    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testCases.isEmpty)
          _this.deleteTestCase(true);
        else
          _this.openLinkedTestCasesDialog(testCases);
      }
    }
  }
  public fetchLinkedTestCases(stepGroupId) {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "workspaceVersionId:" + this.version.id + ",deleted:false,stepGroupId:" + stepGroupId);
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        _this.openStepGroupDeleteDialog(testCases);
      }
    }
  }

    private openStepGroupDeleteDialog(list) {
    this.translate.get("message.common.confirmation.default").subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          testCaseId: this.testCase?.id,
          description: res,
          isPermanentDelete: true,
          linkedEntityList: list,
          item : "Step group",
          note: this.translate.instant('message.common.confirmation.requirement_type')
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            this.markAsDeleted();
          }
        });
    })
  }


  private openLinkedTestCasesDialog(list) {
    this.translate.get("test_case.linked_with_cases").subscribe((res) => {
      this.matModal.open(LinkedEntitiesModalComponent, {
        width: '568px',
        height: '55vh',
        data: {
          description: res,
          linkedEntityList: list,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }

  get canShowRunResult() {
    return this.testCase && !this.testCase.isStepGroup;
  }

  get canShowBulkActions() {
    return this.selectedStepsList && this.selectedStepsList.length>1;
  }

  openDryRun() {
    this.matModal.open(DryRunFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        testCaseId: this.testCase.id
      },
    })
  }

  linkXrayId(mapping: EntityExternalMapping){
    this.entityExternalMappingService.findAll("externalId:"+mapping.externalId).subscribe(res =>{
      if(res.content.length > 0 && res.content[0].entityId!=this.testCaseId){
        this.showWarning(mapping);
      } else{
        this.checkAllTestCasesAreLinked(mapping);
      }
    })
  }

  checkAllTestCasesAreLinked(mapping : EntityExternalMapping){
    this.testCaseService.show(this.testCaseId).subscribe(res=>{
      if(res?.preRequisiteCase !=null){
        this.entityExternalMappingService.findAll("entityId:"+ res?.preRequisiteCase.id).subscribe(res =>{
          if(res.content.length == 0){
            this.translate.get("message.common.prerequisite_not_linked").subscribe((res: string) => {
              this.showNotification(NotificationType.Alert, res);
            });
          }
        })
      }
      this.linkXrayWithTestCase(mapping);
    })
  }

  showWarning(mapping: EntityExternalMapping){
    const dialogRef = this.matModal.open(XrayKeyWarningComponent, {
      width: '450px',
      panelClass: ['matDialog', 'rds-none'],
      data: {entityType: this.entityType}
    });
    dialogRef.afterClosed().subscribe(res =>{
      if(res===false){
        this.checkAllTestCasesAreLinked(mapping);
      }
    })
  }

  linkXrayWithTestCase(mapping: EntityExternalMapping){
    this.entityExternalMappingService.create(mapping).subscribe(
      (res) => {
        this.entityExternalMapping = res;
        this.translate.get("message.common.xray_link.success", {EntityName: "Test Case" }).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
        });
      },
      (error) => {
        console.log("error", error, error.status);
        if (error.status == "404") {
          this.showNotification(NotificationType.Error, error.error.error);
        } else {
          this.translate.get("message.common.xray_link.failure", {EntityName: "Test Case" }).subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
          });
        }
      }
    )
  }
}
