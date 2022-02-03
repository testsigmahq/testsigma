import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {RestMethod} from "../../enums/rest-method.enum";
import {FormGroup, FormControl, Validators} from "@angular/forms";
import {RestStepEntity} from "../../models/rest-step-entity.model";
import {RestCompareType} from "../../enums/rest-compare-type.enum";
import {TestStepService} from "../../services/test-step.service";
import {RestAuthorization} from "../../enums/rest-authorization.enum";
import {NotificationsService} from "angular2-notifications";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStep} from "../../models/test-step.model";
import {TestStepType} from "../../enums/test-step-type.enum";
import {RestStepAuthorizationValue} from "../../models/rest-step-authorization-value.model";
import {TestDataService} from "../../services/test-data.service";

@Component({
  selector: 'app-test-step-rest-form',
  templateUrl: './test-step-rest-form.component.html',
  styles: []
})
export class TestStepRestFormComponent extends BaseComponent implements OnInit {
  @Input('testStep') testStep: TestStep;
  @Input('stepForm') restStepForm: FormGroup;
  @Output('onCancel') onCancel = new EventEmitter<void>();
  @Output('onSave') onSave = new EventEmitter<TestStep>();
  @Output('onSubmitted') onSubmitted = new EventEmitter<void>();
  public activeTab: string = 'Request';
  public isJSONResponse: boolean = false;
  public response: any = null;
  public restStep: RestStepEntity;
  public formSubmitted: boolean = false;
  public saving: Boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testStepService: TestStepService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    if(this.restStepForm.getRawValue().authorizationType == null){
      Object.keys(this.restStepForm.controls).forEach(control => {
        if(control != 'action'){
          this.restStepForm.removeControl(control);
        }
      })
    }
    this.restStepForm.addControl('action', new FormControl(this.testStep.action, [Validators.required]));
    if (!this.testStep.id) {
      this.createStep();
    } else {
      this.restStep = this.testStep.restStep;
      this.restStepForm.patchValue({status: this.restStep.status})
    }
  }

  createStep() {
    this.restStep = new RestStepEntity();
    this.restStep.followRedirects = true;
    this.restStep.method = RestMethod.GET;
    this.restStep.authorizationType = RestAuthorization.NONE;
    this.restStep.authorizationValue = new RestStepAuthorizationValue();
    this.restStep.responseCompareType = RestCompareType.STRICT;
    this.restStep.storeMetadata = true;
    this.testStep.type = TestStepType.REST_STEP;
    this.testStep.restStep = this.restStep;
  }

  setRequestValue() {
    this.testStep.action = this.restStepForm.getRawValue()['action'];
    if (this.restStepForm.invalid || !this.testStep?.action?.length || (!this.restStepForm.controls['status'] && !this.restStep.status)) {
      this.formSubmitted = true;
      if (this.restStepForm.controls['url'].invalid) {
        this.activeTab = 'Request'
      } else if (!this.restStepForm.controls['status'] || this.restStepForm.controls['status'].invalid) {
        this.activeTab = 'Verify'
      }
      return false
    }
    let bodyRuntimeData, headerRuntimeData, storeMetaData, responseHeaders;
    if(!this.restStepForm.controls.storeMetadata){
      bodyRuntimeData = this.testStep.restStep.bodyRuntimeData;
      headerRuntimeData = this.testStep.restStep.headerRuntimeData;
      storeMetaData = this.testStep.restStep.storeMetadata;
    }
    if(!this.restStepForm.controls.responseHeaders){
      responseHeaders = this.testStep.restStep.responseHeaders;
    }
    this.testStep.restStep = new RestStepEntity().deserializeRawValue(this.restStepForm.getRawValue());
    if(this.testStep.restStep.authorizationType == RestAuthorization.BASIC){
      delete this.testStep.restStep.authorizationValue.token
    } else if(this.testStep.restStep.authorizationType == RestAuthorization.BEARER) {
      delete this.testStep.restStep.authorizationValue.password
      delete this.testStep.restStep.authorizationValue.username
    } else {
      delete this.testStep.restStep.authorizationValue
    }
    this.testStep.deserializeCommonProperties(this.restStepForm.getRawValue());
    if(!this.restStepForm.controls.storeMetadata){
      this.testStep.restStep.bodyRuntimeData = bodyRuntimeData;
      this.testStep.restStep.headerRuntimeData = headerRuntimeData;
      this.testStep.restStep.storeMetadata = storeMetaData;
    }
    if(!this.restStepForm.controls.responseHeaders){
      this.testStep.restStep.responseHeaders = responseHeaders;
    }
    return true;
  }

  save() {
    if(!this.setRequestValue()) {
      if(!this.testStep?.action?.length)
        this.onSubmitted.emit()
      return false;
    }
    this.saving = true;
    this.testStepService.create(this.testStep).subscribe((step) => {
      step.parentStep = this.testStep.parentStep;
      step.siblingStep = this.testStep.siblingStep;
      step.stepDisplayNumber = this.testStep.stepDisplayNumber;
      this.onSave.emit(step);
      this.saving = false;
    }, error => {
      this.translate.get('message.common.created.failure', {FieldName: 'Test Step'}).subscribe((res) => {
        this.showAPIError(error, res);
        this.saving = false;
      })
    })
  }

  update() {
    if(!this.setRequestValue()) {
      if(!this.testStep?.action?.length)
        this.onSubmitted.emit()
      return false;
    }
    this.saving = true;
    this.testStepService.update(this.testStep).subscribe((step) => {
      step.parentStep = this.testStep.parentStep;
      step.siblingStep = this.testStep.siblingStep;
      step.stepDisplayNumber = this.testStep.stepDisplayNumber;
      this.onSave.emit(step);
      this.saving = false
    }, error => {
      this.translate.get('message.common.update.failure', {FieldName: 'Test Step'}).subscribe((res) => {
        this.showAPIError(error, res);
        this.saving = false;
      })
    })
  }

  cancel() {
    this.onCancel.emit();
  }

  setResponse(details: any) {
    this.response = details.response;
    this.isJSONResponse = details.isJSONResponse;
  }
}
