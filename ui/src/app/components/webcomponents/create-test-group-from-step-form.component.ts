import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TestCase} from "../../models/test-case.model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TestStep} from "../../models/test-step.model";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TestCaseService} from "../../services/test-case.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStepService} from "../../services/test-step.service";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";

@Component({
  selector: 'app-copy-test-case',
  templateUrl: './create-test-group-from-step-form.component.html',
  styles: []
})
export class CreateTestGroupFromStepFormComponent extends BaseComponent implements OnInit {
  public stepGroupForm: FormGroup;
  public saving: boolean = false;
  @ViewChild('nameInput') searchInput: ElementRef;

  constructor(
    private dialogRef: MatDialogRef<CreateTestGroupFromStepFormComponent>,
    @Inject(MAT_DIALOG_DATA) public options: {
      testCase: TestCase,
      steps: TestStep[]
    },
    private testCaseService: TestCaseService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public testStepService: TestStepService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.addValidations();
  }

  containsStepGroup(){
    return this.options.steps.filter(step => step.isStepGroup).length;
  }

  containsLoop(){
    return this.options.steps.filter(step => step.isForLoop || step.isConditionalWhileLoop).length;
  }

  containsWhileLoop(){
    return this.options.steps.filter(step => step.isWhileLoop).length;
  }

  containsLoopCondition(){
    return this.options.steps.filter(step => step.conditionType == TestStepConditionType.LOOP_WHILE).length;
  }
  containsIf(){
    return this.options.steps.filter(step => step.conditionType == TestStepConditionType.CONDITION_IF).length;
  }

  containsElseIfOrElse(){
    return this.options.steps.filter(step => (step.conditionType == TestStepConditionType.CONDITION_ELSE || step.conditionType == TestStepConditionType.CONDITION_ELSE_IF)).length;
  }

  containsParentId(){
    return this.options.steps.filter(step => step.parentId!=null).length;
  }

  canBtnDisable(){
    return (this.containsElseIfOrElse() && !this.containsIf()) ||
      (this.containsParentId() && !(this.containsParentId() && ((!this.containsLoop() && this.containsIf()) || (!this.containsIf() && this.containsLoop()) || (this.containsIf() && this.containsLoop()))));
  }

  addValidations() {
    this.translate.get('test_step.copy_as.name', {testCaseName: this.options.testCase.name}).subscribe((res: string) => {
      this.stepGroupForm = new FormGroup({
        name: new FormControl(res, [
          Validators.required, Validators.minLength(4), Validators.maxLength(250)
        ])
      });
      setTimeout(() => this.searchInput.nativeElement.focus(), 200);
    })
  }

  saveAsStepGroup(isReplace?: boolean) {
    this.saving = true;
    let copyStepGroup = {
      name: this.stepGroupForm.get('name').value,
      testCaseId: this.options.testCase.id,
      stepIds: this.options.steps.map(step => step.id),
      isStepGroup: true,
      isReplace: isReplace
    }
    this.testCaseService.copy(copyStepGroup).subscribe(
      (testCase) => {
        this.translate.get('test_step.copy_as.step_group.success').subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.dialogRef.close({testCase: testCase, isReplace: isReplace});
          this.saving = false;
        });
      },
      (exception) => {
        this.translate.get('message.common.created.failure', {FieldName: 'Step Group'}).subscribe((res) => {
          this.showAPIError(exception, res, 'Step Group');
          this.saving = false;
        });
      }
    );
  }

  get checkSelectionContinue() {
    let lastPosition = null;
    if(this.options.steps.length > 1) {
      let sortedByPosition = this.options.steps.sort((test: TestStep, test2: TestStep) => test.position - test2.position);
      let isContinueSteps = sortedByPosition.filter((item: TestStep) => {
        lastPosition = lastPosition == null ? item.position : lastPosition
        if ((lastPosition != item.position && item.position - lastPosition == 1) || lastPosition == item.position) {
          lastPosition = item.position;
          return false;
        }
        return true
      });
      return !isContinueSteps.length;
    } else {
      return true;
    }
  }
}
