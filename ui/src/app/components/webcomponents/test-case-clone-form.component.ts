import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TestCase} from "../../models/test-case.model";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TestCaseService} from "../../services/test-case.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStepService} from "../../services/test-step.service";
import {BaseComponent} from "../../shared/components/base.component";

@Component({
  selector: 'app-test-case-clone-form',
  templateUrl: './test-case-clone-form.component.html',
  styles: []
})
export class TestCaseCloneFormComponent extends BaseComponent implements OnInit {

  public stepGroupForm: FormGroup;
  public testCase: TestCase;
  @ViewChild('nameInput') searchInput: ElementRef;

  constructor(
    private dialogRef: MatDialogRef<TestCaseCloneFormComponent>,
    @Inject(MAT_DIALOG_DATA) public options: {
      testCase: TestCase
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
    this.testCase = this.options.testCase;
    this.addValidations();
  }

  addValidations() {
    this.translate.get('test_step.copy_as.name', {testCaseName: this.options.testCase.name}).subscribe((res: string) => {
      this.stepGroupForm = new FormGroup({
        name: new FormControl(res, [
          Validators.required
        ]),
        isStepGroup: new FormControl(false, [Validators.required])
      });
      setTimeout(() => this.searchInput.nativeElement.focus(), 200);
    })
  }

  saveAsTestCase() {

    let copyStepGroup = {
      name: this.stepGroupForm.get('name').value,
      testCaseId: this.options.testCase.id,
      isStepGroup: this.stepGroupForm.get('isStepGroup').value
    }

    let messageKey, fieldName1, fieldName2;
    fieldName1 = this.testCase.isStepGroup? 'Step Group' : 'Test Case'
    if(this.testCase.isStepGroup != copyStepGroup.isStepGroup) {
      fieldName2 = copyStepGroup.isStepGroup? 'Step Group' : 'Test Case';
      messageKey = 'message.common.copied_as.success';
    } else {
      messageKey = 'message.common.copied.success';
    }

    this.testCaseService.copy(copyStepGroup).subscribe(
      (testCase) => {
        this.translate.get(messageKey,{FieldName1: fieldName1, FieldName2: fieldName2}).subscribe(res => {
          this.showNotification(NotificationType.Success, res);
          this.dialogRef.close(testCase);
        });
      },
      (exception) => {
        let testCaseTpe = this.testCase.isStepGroup? 'Step Group' : 'Test Case'
        this.translate.get('message.common.copied.failure', {FieldName: testCaseTpe,FieldName2:copyStepGroup.name}).subscribe(res => {
          this.showAPIError(exception, res, testCaseTpe);
        });
      }
    );
  }

}
