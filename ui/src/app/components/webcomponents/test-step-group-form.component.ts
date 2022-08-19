import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestStep} from "../../models/test-step.model";
import {TestCase} from "../../models/test-case.model";
import {Page} from "../../shared/models/page";
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStepService} from "../../services/test-step.service";
import {TestCaseService} from "../../services/test-case.service";
import {TestStepType} from "../../enums/test-step-type.enum";
import {BaseComponent} from "../../shared/components/base.component";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseStatus} from "../../enums/test-case-status.enum";
import {group} from "@angular/animations";

@Component({
  selector: 'app-test-step-group-form',
  templateUrl: './test-step-group-form.component.html',
  styles: []
})
export class TestStepGroupFormComponent extends BaseComponent implements OnInit {
  @Input('testStep') testStep: TestStep;
  @Input('version') version: WorkspaceVersion;
  @Input('stepForm') stepGroupForm: FormGroup;
  @Output('onCancel') onCancel = new EventEmitter<void>();
  @Output('onSave') onSave = new EventEmitter<TestStep>();
  public stepGroups: Page<TestCase>;
  public searchQuery: string;
  public saving: boolean;
  public isFetching: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testStepService: TestStepService,
    public testCaseService: TestCaseService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.saving = false;
    this.isFetching = true;
    if (!this.testStep.id)
      this.createStep()
    this.addControls();
    this.fetchStepGroups();
  }

  fetchStepGroups(term?) {
    this.isFetching = true;
    if (term) {
      this.searchQuery = ",name:*" + term + "*";
    } else {
      this.searchQuery = '';
    }
    this.testCaseService.findAll("workspaceVersionId:" + this.version.id +
      ",isStepGroup:" + true + ",status:" + TestCaseStatus.READY + this.searchQuery+ (!this.testStep.id? ",deleted:false": "")).subscribe(res => {
      this.stepGroups = res;
      if (this.stepGroups && this.stepGroups.content.length && !this.testStep.stepGroupId) {
        this.testStep.stepGroup = this.stepGroups.content[0];
        this.testStep.stepGroupId = this.stepGroups.content[0].id;
        this.stepGroupForm.patchValue({'stepGroupId': this.testStep.stepGroupId});
      } else if(this.testStep.stepGroupId) {
        if(!this.stepGroups.content.find(group => group.id == this.testStep.stepGroupId)) {
          this.testCaseService.show(this.testStep.stepGroupId).subscribe(res => {
            this.stepGroups.content.push(res);
            this.testStep.stepGroup = res;
          })
        }
      }
      this.isFetching = false;
    })
  }

  save() {
    this.saving = true;
    this.testStep.stepGroupId = this.stepGroupForm.get('stepGroupId').value;
    this.testStep.deserializeCommonProperties(this.stepGroupForm.getRawValue());
    this.testStepService.create(this.testStep).subscribe((step) => {
      step.stepGroup = this.testStep.stepGroup;
      step.parentStep = this.testStep.parentStep;
      step.siblingStep = this.testStep.siblingStep;
      step.stepDisplayNumber = this.testStep.stepDisplayNumber;
      this.saving = false;
      this.onSave.emit(step);
    }, error => {
      this.translate.get('message.common.created.failure', {FieldName: 'Test Step'}).subscribe((res) => {
        this.showAPIError(error, res);
        this.saving = false;
      })
    })
  }

  update() {
    this.saving = true;
    this.testStep.stepGroupId = this.stepGroupForm.get('stepGroupId').value;
    this.testStep.deserializeCommonProperties(this.stepGroupForm.getRawValue());
    this.testStepService.update(this.testStep).subscribe((step) => {
      step.stepGroup = this.testStep.stepGroup;
      step.parentStep = this.testStep.parentStep;
      step.siblingStep = this.testStep.siblingStep;
      step.stepDisplayNumber = this.testStep.stepDisplayNumber;
      this.onSave.emit(step);
      this.saving = false;
    }, error => {
      this.translate.get('message.common.update.failure', {FieldName: 'Test Step'}).subscribe((res) => {
        this.showAPIError(error, res);
        this.saving = false;
      })
    })
  }

  setStepGroup(testCase: TestCase) {
    this.testStep.stepGroup = testCase;
  }

  cancel() {
    delete this.testStep.stepGroup;
    this.testStep.stepGroup = this.stepGroups.content.find(stepGroup => stepGroup.id == this.testStep.stepGroupId);
    this.onCancel.emit();
  }

  private addControls() {
    this.stepGroupForm.addControl(
      'stepGroupId', new FormControl(this.testStep.stepGroupId, [])
    );
  }
  get stepGroupDescriptionText(){
    const tempDescElem = document.createElement('div');
    tempDescElem.innerHTML = ( this.testStep?.stepGroup?.description || '' ) as string;
    return tempDescElem?.textContent;
  }

  private createStep() {
    this.testStep.type = TestStepType.STEP_GROUP;
  }
}
