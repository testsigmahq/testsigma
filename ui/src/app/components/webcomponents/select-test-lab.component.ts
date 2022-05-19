import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {IntegrationsService} from "../../shared/services/integrations.service";
import {Integrations} from "../../shared/models/integrations.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";

@Component({
  selector: 'app-select-test-lab',
  template: `
    <div
      *ngIf="selectTestLabForm"
      class="form-group pb-lg" [formGroup]="selectTestLabForm">
      <div
        class="fz-14 rb-medium pb-18">
        <span
          [translate]="'test_plan.environment.lab.title'"></span>
        <div
          class="dropdown mouse-over d-inline-block">
          <div class="btn-group pointer">
            <i class="fa-question-circle-solid text-t-secondary pl-7"></i>
          </div>
          <div
            class="dropdown-menu"
            style="box-shadow: none;margin-top: -6px;padding-top: .5rem;background:
         transparent;min-width:350px;left: -3px;">
            <div
              class="bg-white border-rds-4 ng-scope p-25 shadow-all2-b4 d-flex">
              <i class="fa-help fz-18 text-t-secondary"></i>
              <ul class="rb-regular-i-d ts-col-90 ml-auto my-0 p-0 theme-text" style="list-style: none">
                <li [innerHTML]="'test_plan.form.help.testsigma_lab' | translate"></li>
                <li class="pt-4" [innerHTML]="'test_plan.form.help.hybrid' | translate"></li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <div class="test-lab-container">
        <mat-radio-group
          aria-labelledby="example-radio-group-label"
          class="example-radio-group d-flex" formControlName="testPlanLabType">
          <mat-radio-button
            (change)="setTargetMachineAsMandatory(false)"
            *ngIf="((testPlan?.id && testPlan.isTestsigmaLab) || !testPlan?.id) && applications && !isRest"
            [value]="'TestsigmaLab'">
            <div class="lab-item">
              <span class="testsigma-lab-logo lab-icon"></span>
              <span style="font-style: normal;font-size: 14px;font-weight: 500;line-height: 26px;"
                    [translate]="'execution.lab_type.TestsigmaLab'"></span>
            </div>
          </mat-radio-button>
          <mat-radio-button
            (change)="setTargetMachineAsMandatory(true)"
            *ngIf="((testPlan?.id && testPlan.isHybrid) || !testPlan?.id )"
            [value]="'Hybrid'">
            <div class="lab-item">
              <span class="testsigma-local-devices-logo lab-icon"></span>
              <span [translate]="'execution.lab_type.Hybrid'"></span>
            </div>
          </mat-radio-button>
          <mat-radio-button
            (change)="setTargetMachineAsMandatory(false)"
            *ngIf="((testPlan?.id && testPlan.isPrivateLab) || !testPlan?.id) && applications && isPrivateLabInstalled && isWeb"
            [value]="'PrivateGrid'">
            <div class="lab-item">
              <span class="grid lab-icon"></span>
              <span [translate]="'execution.lab_type.PrivateGrid'"></span>
            </div>
          </mat-radio-button>
        </mat-radio-group>
      </div>
      <div class="d-flex mt-15" *ngIf="isWeb && isHybrid" [formGroup]="selectTestLabForm">
        <mat-checkbox
          class="mat-checkbox"
          id="matchBrowserVersion"
          [formControlName]="'matchBrowserVersion'">
          <p class="md-title"
             [translate]="'test_plan.hybrid.match_browser_version'"></p>
        </mat-checkbox>
      </div>
      <div class="d-flex mt-20" *ngIf="!isDry && isTestsigmaLab && !authGuard.openSource.isEnabled">
        <div style="font-size: 1.2em;">
          <i class="fa-help mr-5"></i>
        </div>
        <div style="font-size:medium;font-weight: 400;" class="ml-5">
            <span [innerHTML]="'test_plan.testsigmalab.requires_access_to_cloud' | translate"></span>
            <a (click)="closeDialog()" [innerHTML]="'message.common.click_here' | translate" [routerLink]="['/settings/testsigma']"></a>
            <a [innerHTML]="'test_plan.testsigmalab.learn_more' | translate"
               href="https://testsigma.com/docs/getting-started/testsigma-community-cloud/" target="_blank"></a>
            <span [innerHTML]="'test_plan.testsigmalab.communityEdition' | translate"></span>
        </div>
      </div>
      <div class="d-flex mt-15" *ngIf="isDry && isTestsigmaLab && !authGuard.openSource.isEnabled">
        <div style="font-size:1.8em;font-weight: 400;margin-top: 200px; line-height: 1.5" class="mx-auto w-60 text-center">
        <span [innerHTML]="'dry_test_plan.testsigamlab.requires_access_to_cloud' | translate"></span>
          <br><br>
          <span>
            <a (click)="closeDialog()" [innerHTML]="'message.common.click_here' | translate" [routerLink]="['/settings/storage']"></a>
            <span [innerHTML]="'test_plan.testsigmalab.enable' | translate"></span>
          </span>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class SelectTestLabComponent implements OnInit {
  @Input('formGroup') selectTestLabForm: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlan') testPlan: TestPlan;
  @Input('isDry') isDry : boolean;
  @Output() closeDryRunDialog = new EventEmitter<void>();

  public applications: Integrations[];

  constructor(
    private integrationsService: IntegrationsService,
    public authGuard: AuthenticationGuard) {
  }

  ngOnInit(): void {
    this.integrationsService.findAll().subscribe(res => {
      this.applications = res;
      if(this.isNewTestPlan) {
        if (!this.isTestsigmaLabInstalled) {
          this.selectTestLabForm.controls['testPlanLabType'].setValue(TestPlanLabType.Hybrid)
        } else {
          this.selectTestLabForm.controls['testPlanLabType'].setValue(TestPlanLabType.TestsigmaLab)
        }
      }
    });
  }
  get isNewTestPlan(){
    return !this.selectTestLabForm.controls['testDevices'];
  }

  get isTestsigmaLabInstalled() {
    return this.applications?.find(app => app.isTestsigmaLab);
  }

  get isRest() {
    return this.version?.workspace?.isRest;
  }

  get isWeb() {
    return this.version?.workspace?.isWeb;
  }

  get isIOS() {
    return this.version?.workspace?.isIosNative;
  }

  get isHybrid() {
    return this.selectTestLabForm.controls['testPlanLabType'].value === TestPlanLabType.Hybrid;
  }

  get isTestsigmaLab() {
    return this.selectTestLabForm.controls['testPlanLabType'].value === TestPlanLabType.TestsigmaLab;
  }

  get isPrivateLabInstalled() {
    return this.applications?.find(app => app.isPrivateLab);
  }

  setTargetMachineAsMandatory(mandatory) {
    if (this.isRest && this.selectTestLabForm != undefined) {
      if (mandatory) {
        this.selectTestLabForm.controls.environments["controls"][0].controls.agentId.enable();
      } else {
        this.selectTestLabForm.controls.environments["controls"][0].controls.agentId.disable()
      }
    }
  }

  closeDialog(){
    this.closeDryRunDialog.emit();
  }
}
