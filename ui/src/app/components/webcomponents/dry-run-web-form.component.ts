import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {DryTestPlan} from "../../models/dry-test-plan.model";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {Environment} from "../../models/environment.model";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";

@Component({
  selector: 'app-dry-run-web-form',
  templateUrl: './dry-run-web-form.component.html',
  styles: []
})
export class DryRunWebFormComponent extends BaseComponent implements OnInit {

  @Input('formGroup') webForm: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('execution') execution: DryTestPlan;
  @Input('environment') environment?: Environment;
  @Output("closeDialog")closeDryRunDialog = new EventEmitter<any>();

  constructor(public authGuard: AuthenticationGuard) {
    super(authGuard);
  }

  get isHybrid() {
    return this.webForm.controls['testPlanLabType'].value == TestPlanLabType.Hybrid;
  }

  get environmentFormGroup(): FormGroup {
    return (<FormGroup>(<FormGroup>(<FormGroup>(<FormGroup>this.webForm.controls['environments']).controls[0])));
  }

  get testPlanLabType(): TestPlanLabType {
    return <TestPlanLabType>this.webForm.controls['testPlanLabType'].value;
  }

  get isRest() {
    return this?.version?.workspace?.isRest
  }

  ngOnInit() {
  }

  ngOnChanges(): void {
    this.environmentFormGroup.removeControl('platform');
    this.environmentFormGroup.removeControl('resolution');
    this.environmentFormGroup.removeControl('osVersion');
    this.environmentFormGroup.removeControl('browser');
    this.environmentFormGroup.removeControl('browserVersion');
    this.environmentFormGroup.addControl('platform', new FormControl(this.execution.environments[0].platform, [this.requiredIfValidator(() => !this.isRest)]));
    this.environmentFormGroup.addControl('resolution', new FormControl(this.execution.environments[0].resolution, []));
    this.environmentFormGroup.addControl('osVersion', new FormControl(this.execution.environments[0].osVersion, []));
    this.environmentFormGroup.addControl('browser', new FormControl(this.execution.environments[0].browser, [this.requiredIfValidator(() => !this.version?.workspace.isMobileNative && !this.isRest && this.isHybrid)]));
    this.environmentFormGroup.addControl('browserVersion', new FormControl(this.execution.environments[0].browserVersion, []));
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
