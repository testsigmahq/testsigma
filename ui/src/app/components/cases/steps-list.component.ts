import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";
import {fromEvent} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {TestStep} from "../../models/test-step.model";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {Page} from "../../shared/models/page";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {CreateTestGroupFromStepFormComponent} from "../webcomponents/create-test-group-from-step-form.component";
import {StepBulkUpdateFormComponent} from "../webcomponents/step-bulk-update-form.component";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {BaseComponent} from "../../shared/components/base.component";
import {TestStepService} from "../../services/test-step.service";
import {TestCaseActionStepsComponent} from "../webcomponents/test-case-action-steps.component";
import {TestStepType} from "../../enums/test-step-type.enum";
import {DryRunFormComponent} from "../webcomponents/dry-run-form.component";
import {TestStepPriority} from "../../enums/test-step-priority.enum";

import {AddonActionService} from "../../services/addon-action.service";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";
import {StepActionType} from "../../enums/step-action-type.enum";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-steps-list',
  templateUrl: './steps-list.component.html',
  styles: []
})
export class StepsListComponent extends BaseComponent implements OnInit {
  public testCase: TestCase;
  public isSearchEnable: boolean = false;
  public canDrag: boolean = false;
  public searchTerm: string;
  public stepLength: number;
  public selectedStepsList: TestStep[];
  public draggedSteps: TestStep[];
  @ViewChild('searchInput') searchInput: ElementRef;
  @ViewChild(TestCaseActionStepsComponent)
  private actionStepsComponent: TestCaseActionStepsComponent;
  public version: WorkspaceVersion;
  public templates: Page<NaturalTextActions>;
  public bulkStepUpdateDialogRef: MatDialogRef<StepBulkUpdateFormComponent, any>;
  public createStepGroupFromPopUp: MatDialogRef<CreateTestGroupFromStepFormComponent, any>;
  public currentStepType: string;
  private stepCreateArticles = {
    "WebApplication": "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/",
    "MobileWeb": "https://testsigma.com/docs/test-cases/create-steps-recorder/web-apps/",
    "AndroidNative": "https://testsigma.com/docs/test-cases/create-steps-recorder/android-apps/",
    "IOSNative": "https://testsigma.com/docs/test-cases/create-steps-recorder/ios-apps/",
    "Rest": "https://testsigma.com/tutorials/getting-started/automate-rest-apis/"
  }
  private stepVideoResources = {
    "WebApplication": "https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/web/create-steps.mp4",
    "MobileWeb": "https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/mobile-web/create-steps.mp4",
    "AndroidNative": "https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/android/create-steps.mp4",
    "IOSNative": "https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/ios/create-steps.mp4",
    "Rest": "https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/rest-api/create-steps.mp4"
  }
  public stepArticleUrl = "";
  public stepVideoUrl = "";
  public isRibbonShowed: boolean = true;
  public isHelpWidgetShowed: boolean = true;
  public selectedTemplate: NaturalTextActions;
  public isCheckHelpPreference = false;
  inputValue: any;
  public addonAction: Page<AddonNaturalTextAction>;
  public saving: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private testCaseService: TestCaseService,
    private versionService: WorkspaceVersionService,
    private naturalTextActionsService: NaturalTextActionsService,
    private matDialog: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testStepService: TestStepService,
    private AddonActionService: AddonActionService,
    ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.route.parent.params.subscribe((params: Params) => {
      this.fetchTestCase(params.testCaseId);
    })
  }

  fetchTestCase(id: number) {
    this.testCaseService.show(id).subscribe(res => {
      this.testCase = res;
      this.fetchVersion();
    });
    this.attachDebounceEvent();
  }

  fetchVersion() {
    this.versionService.show(this.testCase.workspaceVersionId).subscribe(res => {
      this.version = res;
      this.currentStepType = this.currentStepType ? this.currentStepType :
        this.version.workspace.isRest ? TestStepType.REST_STEP : TestStepType.ACTION_TEXT;

      this.stepArticleUrl = this.stepCreateArticles[this.version?.workspace?.workspaceType];
      this.stepVideoUrl = this.stepVideoResources[this.version?.workspace?.workspaceType];
      this.fetchAddonAction();
      this.fetchNLActions();
    })
  }

  fetchNLActions(subQuery?) {
    let workspaceType: WorkspaceType = this.version.workspace.workspaceType;
    this.naturalTextActionsService.findAll("workspaceType:" + workspaceType+(subQuery? subQuery:'')).subscribe(res => this.templates = res);
  }

  fetchAddonAction(subQuery?) {
    let workspaceType: WorkspaceType = this.version.workspace.workspaceType;
    this.AddonActionService.findAll("workspaceType:" + workspaceType+(subQuery? subQuery :'')+",status!UNINSTALLED").subscribe(res => {
      this.addonAction = res;
    })
  }

  focusOnSearch() {
    this.attachDebounceEvent();
  }
  clearSearch() {
    this.searchTerm = undefined;
    this.inputClear();
  }
  inputClear(){
    setTimeout(() => this.searchInput.nativeElement.value = null,500);
    this.inputValue = null;
  }

  attachDebounceEvent() {
    if (this.searchInput && this.searchInput.nativeElement)
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            if (this.searchInput?.nativeElement?.value) {
              this.searchTerm = this.searchInput.nativeElement.value;
            } else {
              this.searchTerm = undefined;
            }
          })
        )
        .subscribe();
    else
      setTimeout(() => {
        this.attachDebounceEvent();
      }, 100);
  }

  get canShowRunResult() {
    return this.testCase && !this.testCase.isStepGroup;
  }

  get canShowBulkActions() {
    return this.selectedStepsList && this.selectedStepsList.length>1;
  }

  setStepLength(number: number) {
    this.stepLength = number;
    this.testCaseService.emitStepLength(this.stepLength);
  }

  selectedSteps(steps: TestStep[]) {
    this.selectedStepsList = steps;
  }

  createStepGroupFrom() {
    this.createStepGroupFromPopUp = this.matDialog.open(CreateTestGroupFromStepFormComponent, {
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        testCase: this.testCase,
        steps: this.selectedStepsList
      }
    });
    this.createStepGroupFromPopUp.afterClosed().subscribe(testCase => {
      if(testCase?.id)
        window.open('/ui/td/cases/'+testCase.id+"/steps", '_blank');
    })
  }

  openBulkUpdate() {
    this.bulkStepUpdateDialogRef = this.matDialog.open(StepBulkUpdateFormComponent, {
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        steps: this.selectedStepsList
      }
    })
    this.bulkStepUpdateDialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.selectedStepsList = [];
        this.testCase = undefined;
        this.fetchTestCase(this.route.parent.snapshot.params.testCaseId);
      }
    })
  }

  bulkDeleteConfirm() {
    this.translate.get("message.common.confirmation.message", {FieldName: 'Test Steps'}).subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result)
          this.bulkDestroy();
      });
    })
  }

  bulkDestroy() {
    this.testStepService.bulkDestroy(this.selectedStepsList).subscribe({
      next: () => {
        this.fetchTestCase(this.route.parent.snapshot.params.testCaseId);
        this.translate.get("message.common.deleted.success", {FieldName: 'Test Steps'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.selectedStepsList = [];
        });
      },
      error: (error) => {
        if (error.status == "400") {
          this.showNotification(NotificationType.Error, error.error);
        } else {
          this.translate.get("message.common.deleted.failure", {FieldName: 'Test Steps'}).subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
          });
        }
      }
    })
  }

  onPositionChange(steps: TestStep[]) {
    this.draggedSteps = steps;
  }

  cancelDragging() {
    this.draggedSteps = [];
    this.canDrag = false;
    this.fetchTestCase(this.route.parent.snapshot.params.testCaseId);
  }

  updateSorting() {
    this.saving = true;
    this.testStepService.bulkUpdate(this.draggedSteps).subscribe(() => {
      this.translate.get('testcase.details.steps.re-order.success').subscribe(key => {
        this.showNotification(NotificationType.Success, key);
        this.cancelDragging();
        this.saving = false;
      })
    }, error => {
      this.translate.get('testcase.details.steps.re-order.failure').subscribe((res) => {
        this.showAPIError(error, res);
        this.saving = false;
      })
    })
  }

  onStepType(obj) {
    this.currentStepType = obj;
    let step = obj.step;
    if((step?.conditionType == TestStepConditionType.LOOP_WHILE && step?.isEditing) || step?.type == TestStepType.WHILE_LOOP ) {
      let query = ",stepActionType:"+ StepActionType.WHILE_LOOP;
      this.fetchNLActions(query);
      this.fetchAddonAction(query)
    } else {
      this.fetchNLActions();
      this.fetchAddonAction()
    }
  }

  get isActionText() {
    return this.currentStepType == TestStepType.ACTION_TEXT;
  }

  get isRest() {
    return this.currentStepType == TestStepType.REST_STEP;
  }

  get isStepGroup() {
    return this.currentStepType == TestStepType.STEP_GROUP;
  }

  get isForLoop() {
    return this.currentStepType == TestStepType.FOR_LOOP;
  }

  get isRecording() {
    return this.currentStepType == 'record';
  }


  openDryRun() {
    this.matDialog.open(DryRunFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        testCaseId: this.testCase.id
      },
    })
  }

  onSelectTemplate(template: NaturalTextActions) {
    this.selectedTemplate = template
  }

}
