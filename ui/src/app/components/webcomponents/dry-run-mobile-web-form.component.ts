import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {DryTestPlan} from "../../models/dry-test-plan.model";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {Environment} from "../../models/environment.model";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {Platform} from "../../enums/platform.enum";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-dry-run-mobile-web-form',
  templateUrl: './dry-run-mobile-web-form.component.html',
  styles: []
})
export class DryRunMobileWebFormComponent extends BaseComponent implements OnInit {
  @Input('formGroup') mobileWebForm: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlan') dryExecution: DryTestPlan;
  @Input('environment') environment?: Environment;
  @Output("closeDialog")closeDryRunDialog = new EventEmitter<any>();

  constructor( public authGuard: AuthenticationGuard,
               public notificationsService: NotificationsService,
               public translate: TranslateService) {
    super(authGuard,notificationsService,translate);
  }

  get environmentFormGroup(): FormGroup {
    return <FormGroup>(<FormGroup>(<FormGroup>this.mobileWebForm.controls['environments']).controls[0]);
  }

  get testPlanLabType(): TestPlanLabType {
    return <TestPlanLabType>this.mobileWebForm.controls['testPlanLabType'].value;
  }

  get isHybrid() {
    return this.mobileWebForm.controls['testPlanLabType'].value == TestPlanLabType.Hybrid;
  }

  get isRest() {
    return this?.version?.workspace?.isRest
  }

  ngOnInit(): void {
    this.environmentFormGroup.addControl('platform', new FormControl(this.dryExecution.environments[0].platform, [this.requiredIfValidator(() => !this.isRest)]));
    this.environmentFormGroup.addControl('browser', new FormControl(this.dryExecution.environments[0].browser, [this.requiredIfValidator(() => !this.version?.workspace.isMobileNative && !this.isRest )]));
    this.environmentFormGroup.addControl('osVersion', new FormControl(this.dryExecution.environments[0].osVersion, [this.requiredIfValidator(() => !this.isRest)]));
    this.environmentFormGroup.addControl('deviceName', new FormControl(this.dryExecution.environments[0].deviceName, [this.requiredIfValidator(() => !this.version?.workspace.isWeb && !this.isRest)]));
    (<FormGroup>(<FormGroup>this.mobileWebForm?.controls['environments']).controls[0]).addControl('deviceId', new FormControl(this.dryExecution.environments[0].deviceId, [this.requiredIfValidator(() => this.isHybrid)]));
  }

  requiredIfValidator(predicate) {
    return (formControl => {
      if (!formControl.parent) {
        return null;
      }
      if (predicate()) {
        return Validators.required(formControl);
      }
      return null;
    })
  }

  closeDialogTab(){
    this.closeDryRunDialog.emit();
  }
}
