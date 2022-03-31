import {Component, OnInit} from '@angular/core';
import {TestCaseService} from "../../services/test-case.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {TestPlanResultService} from "../../services/test-plan-result.service";
//
import {BaseComponent} from "../../shared/components/base.component";
import {TestCase} from "../../models/test-case.model";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
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
import {TestStepService} from "../../services/test-step.service";

@Component({
  selector: 'app-test-case-details',
  templateUrl: './test-case-details.component.html',
  styles: []
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
    private chromeRecorderService : ChromeRecorderService
  ) {
    super(authGuard, notificationsService, translate,toastrService);
  }

  get isGroup() {
    return this.testCase?.isStepGroup ? 'Step Group' : 'Test Case';
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.testCaseId = params.testCaseId;
      this.fetchUserPreference()
      this.fetchTestCase();
    });
    this.testCaseService.getStepLengthEmitter().subscribe(hasInspectorFeatureres => {
      this.stepsLength = hasInspectorFeatureres;
    })
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
    this.translate.get("message.common.confirmation.message", {FieldName: this.isGroup }).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
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
    let fieldName = this.testCase.isStepGroup ? 'Step Group' : 'Test Case';
    this.testCaseService.markAsDeleted(this.testCaseId).subscribe({
        next: () => {
          this.fetchTestCase();
          this.translate.get("message.common.deleted.success", {FieldName: this.isGroup }).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          if (error.status == "400") {
            this.showNotification(NotificationType.Error, error.error);
          } else {
            this.translate.get("message.common.deleted.failure", {FieldName: this.isGroup }).subscribe((res: string) => {
              this.showNotification(NotificationType.Error, res);
            });
          }
        }
      }
    );
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
          this.translate.get("message.common.restore.success", {FieldName: this.isGroup }).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          if (error.status == "400") {
            this.showNotification(NotificationType.Error, error.error);
          } else {
            this.translate.get("message.common.restore.failure", {FieldName: this.isGroup }).subscribe((res: string) => {
              this.showAPIError(error, res, this.isGroup);
            });
          }
        }
      }
    );
  }

  deletePermanently() {
    this.testCaseService.destroy(this.testCaseId).subscribe({ next : () => {
      this.router.navigate(['/td', this.testCase.workspaceVersionId, this.testCase?.testcaseRedirection, 'filter', this.userPreference?.testCaseFilterId]);
    },

    error: (error) => {
          this.showNotification(NotificationType.Error, error && error.error && error.error.error ? error.error.error :
          this.translate.instant("message.component.delete.failure"));
        }
    })
  }

  public fetchLinkedCases() {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, ",stepGroupId:" + this.testCaseId);
    waitTillRequestResponds();
    let _this = this;

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
}
