import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlan} from "../../models/test-plan.model";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatHorizontalStepper} from '@angular/material/stepper';
import {Screenshot} from "../../enums/screenshot.enum";
import {RecoverAction} from "../../enums/recover-action.enum";
import {OnAbortedAction} from "../../enums/on-aborted-action.enum";
import {PreRequisiteAction} from "../../enums/pre-requisite-action.enum";
import {TestPlanService} from "../../services/test-plan.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {Router} from '@angular/router';
import {TestPlanType} from "../../enums/execution-type.enum";
import {ReRunType} from "../../enums/re-run-type.enum";

@Component({
  selector: 'app-test-plan-settings-form',
  templateUrl: './test-plan-settings-form.component.html',
  styles: []
})
export class TestPlanSettingsFormComponent extends BaseComponent implements OnInit {
  @Input('formGroup') formGroup: FormGroup;
  @Output('formSubmitted') public formSubmitted = new EventEmitter<boolean>();
  @Input('version') version: WorkspaceVersion;
  @Input('testPlan') testPlan: TestPlan;
  @Input('stepper') stepper: MatHorizontalStepper;
  public saving: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testPlanService: TestPlanService,
    private router: Router) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.formGroup.addControl('pageTimeOut', new FormControl(this.testPlan.pageTimeOut || 30, [Validators.required]));
    this.formGroup.addControl('elementTimeOut', new FormControl(this.testPlan.elementTimeOut || 30, [Validators.required]));
    this.formGroup.addControl('environmentId', new FormControl(this.testPlan.environmentId, []));
    this.formGroup.addControl('visualTestingEnabled', new FormControl(this.testPlan.visualTestingEnabled, []));
    this.formGroup.addControl('retrySessionCreation', new FormControl(this.testPlan.retrySessionCreation, []));
    this.formGroup.addControl('retrySessionCreationTimeout', new FormControl(this.testPlan.retrySessionCreationTimeout || null, []));
    this.formGroup.addControl('screenshot', new FormControl(this.testPlan.screenshot || Screenshot.FAILED_STEPS, [Validators.required]));
    this.formGroup.addControl('recoveryAction', new FormControl(this.testPlan.recoveryAction || RecoverAction.Run_Next_Testcase, [Validators.required]));
    this.formGroup.addControl('onAbortedAction', new FormControl(this.testPlan.onAbortedAction || OnAbortedAction.Reuse_Session, [Validators.required]));
    this.formGroup.addControl('onStepPreRequisiteFail', new FormControl(this.testPlan.onStepPreRequisiteFail || RecoverAction.Run_Next_Testcase, [Validators.required]));
    this.formGroup.addControl('onTestCasePreRequisiteFail', new FormControl(this.testPlan.onTestCasePreRequisiteFail || PreRequisiteAction.Abort, [Validators.required]));
    this.formGroup.addControl('onSuitePreRequisiteFail', new FormControl(this.testPlan.onSuitePreRequisiteFail || PreRequisiteAction.Abort, [Validators.required]));
    this.formGroup.addControl('reRunType', new FormControl(this.testPlan.reRunType || ReRunType.NONE));
  }

  previous() {
    this.stepper.previous();
  }
  get isRest() {
    return this?.version?.workspace?.isRest
  }

  save() {
    this.formSubmitted.emit();
    this.saving = true;
    let testPlanId = this.testPlan.id;
    let json = this.formGroup.getRawValue();
    this.testPlan = new TestPlan().deserialize(this.formGroup.getRawValue());
    this.testPlan.id = testPlanId;
    this.testPlan.workspaceVersionId = this.version.id;
    this.testPlan.environmentId = json.environmentId==undefined ? null : json.environmentId;
    if(this.testPlan.environmentId == -1) {
      delete this.testPlan.environmentId
    }
    if(this.isRest)
      this.testPlan.testPlanType = TestPlanType.DISTRIBUTED;
    this.testPlan.environments.forEach((env, index) => {
      env.matchBrowserVersion = this.testPlan.matchBrowserVersion
      if(this.version.workspace.isMobileNative){
        if(Boolean(json.environments[index].settings.app_upload_id)) env.settings.appUploadId = json.environments[index].settings.app_upload_id;
        env.settings.appPathType = this.formGroup.getRawValue().environments[index].settings.appPathType;
      }
    });
    if(this.checkNameEnvironment()) {
      this.testPlanService.update(this.testPlan).subscribe(res => {
        this.saving = false;
        this.translate.get('message.common.update.success', {FieldName: 'Test Plan'})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'plans', res.id]);
      }, (exception) => {
        this.saving = false;
        this.translate.get('message.common.update.failure', {FieldName: "Test Plan"})
          .subscribe(res => this.showAPIError(exception, res));
      })
    }
  }

  create() {
    this.formSubmitted.emit();
    this.saving = true;
    let json = this.formGroup.getRawValue();
    this.testPlan = new TestPlan().deserialize(json);
    this.testPlan?.environments?.forEach((environment, index) => {
      environment.testSuites = json.environments[index].suiteIds
      environment.matchBrowserVersion = this.testPlan.matchBrowserVersion;
      if(this.version.workspace.isMobileNative){
        if(Boolean(json.environments[index].settings.app_upload_id)) environment.settings.appUploadId = json.environments[index].settings.app_upload_id;
        environment.settings.appPathType = this.formGroup.getRawValue().environments[index].settings.appPathType;
      }
    })
    if(this.isRest)
      this.testPlan.testPlanType = TestPlanType.DISTRIBUTED;
    this.testPlan.workspaceVersionId = this.version.id;
    if(this.testPlan.environmentId == -1) {
      delete this.testPlan.environmentId
    }

    if(this.checkNameEnvironment()) {
      this.testPlanService.create(this.testPlan).subscribe(res => {
        this.saving = false;
        this.translate.get('message.common.created.success', {FieldName: 'Test Plan'})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.router.navigate(['/td', 'plans', res.id]);
      }, (exception) => {
        this.saving = false;
        this.translate.get('message.common.created.failure', {FieldName: "Test Plan"})
          .subscribe(res => this.showAPIError(exception, res,'Test Plan'));
      })
    }
  }

  checkNameEnvironment() {

    if(!this.testPlan?.name||this.testPlan?.name?.length<4) {
      this.stepper.selectedIndex = 0;
      this.saving = false;
      return false;
    }

    if(!this.testPlan?.environments?.length ||
      this.testPlan?.environments?.filter(environment => !environment?.suiteIds?.length)?.length) {
      this.stepper.selectedIndex = 1;
      this.saving = false;
      return false;
    }
    return true;
  }

}
