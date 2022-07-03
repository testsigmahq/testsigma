import {Component, EventEmitter, Input, OnInit, Optional, Output} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {TestStepType} from "../../enums/test-step-type.enum";
import {BaseComponent} from "../../shared/components/base.component";
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {TestStepForLoop} from "../../models/test-step-for-loop.model";
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";
import {Page} from "../../shared/models/page";

@Component({
  selector: '' +
    'app-test-step-for-loop-form',
  templateUrl: './test-step-for-loop-form.component.html',
  styles: []
})
export class TestStepForLoopFormComponent extends BaseComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @Input('testStep') public testStep: TestStep;
  @Output('onCancel') onCancel = new EventEmitter<void>();
  @Output('onSave') onSave = new EventEmitter<TestStep>();
  @Input('stepForm') stepForm: FormGroup;
  @Optional() @Input("stepRecorderView") stepRecorderView:boolean;
  loopForm: FormGroup = new FormGroup({});
  public testDataList: Page<TestData>;
  public startArray: Array<any>;
  public endArray: Array<any>;
  public saving: boolean;
  public searchQuery: string = '';
  public isFetching: Boolean = false;
  private oldData: TestStepForLoop;
  public currentTestDataList: TestData;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testStepService: TestStepService,
    public testDataService: TestDataService,
    private formBuilder: FormBuilder
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.isFetching = true;
    if (!this.testStep.id)
      this.createStep();
    this.saving = false;
    this.addFormControls();
    this.fetchTestDataProfile(true,"");
  }

  fetchTestDataProfile(setFirst, term?) {
    this.isFetching = true;
    if (term) {
      this.searchQuery = ",testDataName:*" + term + "*";
    } else {
      this.searchQuery = '';
    }
    this.testDataService.findAll("versionId:" + this.version.id + this.searchQuery).subscribe(res => {
      this.testDataList = res;
      if (this.testDataList && this.testDataList.content && this.testDataList.content.length && setFirst) {
        let dataset = this.testDataList.content[0];
        let startIndex:number=-1;
        let endIndex:number=-1;
        if(this.testStep?.id) {
          dataset = this.testDataList.content.find(data => data.id == this.testStep.forLoopTestDataId)
          if(!dataset){
            this.fetchTDPById(dataset, startIndex, endIndex)
          } else {
            startIndex = this.testStep.forLoopStartIndex;
            endIndex = this.testStep.forLoopEndIndex;
          }
        }
        this.oldData = new TestStepForLoop();
        this.oldData.testDataId = dataset.id;
        this.oldData.startIndex = startIndex;
        this.oldData.endIndex = endIndex;
        this.testStep.testData = dataset;
        this.setStartValue(dataset, startIndex, endIndex);
        if (!term) {
          this.addFormControls();
        }
      }
      this.isFetching = false
    })
  }

  fetchTDPById(dataset, startIndex, endIndex) {
    this.testDataService.findAll("versionId:" + this.version.id + this.searchQuery+",id:"+this.testStep?.forLoopTestDataId).subscribe(res => {
      this.testDataList.content = [...this.testDataList.content, ...res.content];
      dataset = this.testDataList.content.find(data => data.id == this.testStep.forLoopTestDataId)
      startIndex = this.testStep.forLoopStartIndex;
      endIndex = this.testStep.forLoopEndIndex;
      this.testStep.testData = dataset;
      this.setStartValue(dataset, startIndex, endIndex);
      this.addFormControls();
    })
  }

  addFormControls() {
    this.loopForm = this.formBuilder.group({
      testDataId: new FormControl(this.testStep.forLoopTestDataId, []),
      startIndex: new FormControl(this.testStep.forLoopStartIndex, []),
      endIndex: new FormControl(this.testStep.forLoopEndIndex, [])
    });
  }

  toggleDataProfile(testData: TestData) {
    this.testStep.testData = testData;
    this.setStartValue(testData, -1, -1);
  }

  toggleStartIndex(endIndex?) {
    let startIndex = this.loopForm.get('startIndex').value || 1;
    let startArray = [...this.startArray]
    if(startIndex == -1)
      this.endArray = startArray;
    else
      this.endArray = startArray.splice(startIndex - 1, startArray.length);
    this.testStep.forLoopEndIndex = endIndex? endIndex : -1;
    if(endIndex){
      this.addFormControls();
    }
    else{
      this.loopForm.patchValue({
        endIndex: endIndex? endIndex : -1
      })
    }
  }

  setStartValue(testData: TestData, startIndex?, endIndex?) {
    let dataSetLength = 1;
    if (testData && testData.data) {
      dataSetLength = testData.data.length;
    }
    this.startArray = Array.from({length: dataSetLength}, (_, i) => i + 1);
    let loopDetails = new TestStepForLoop();
    loopDetails.testDataId = testData.id;
    loopDetails.startIndex = startIndex? startIndex : this.startArray[0];
    loopDetails.endIndex = endIndex ? endIndex: dataSetLength;
    this.testStep.forLoopTestDataId = loopDetails.testDataId;
    this.testStep.forLoopStartIndex = loopDetails.startIndex;
    this.testStep.forLoopEndIndex = loopDetails.endIndex;
    this.toggleStartIndex(endIndex);
  }

  createStep() {
    this.testStep.type = TestStepType.FOR_LOOP;
    this.testStep.priority = TestStepPriority.MINOR;
    this.testStep.conditionType = TestStepConditionType.LOOP_FOR;
  }

  save() {
    this.testStep.deserializeCommonProperties(this.stepForm.getRawValue());
    this.testStep.forLoopTestDataId = this.loopForm.get("testDataId")?.value;
    this.testStep.forLoopStartIndex = this.loopForm.get("startIndex").value;
    this.testStep.forLoopEndIndex = this.loopForm.get("endIndex").value;
    this.testStep.ignoreStepResult = this.testStep.ignoreStepResult === undefined ? true : this.testStep.ignoreStepResult;
    this.saving = true;
    this.testStepService.create(this.testStep).subscribe((step) => {
      step.testData = this.testStep.testData;
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
    this.testStep.deserializeCommonProperties(this.stepForm.getRawValue());
    this.testStep.forLoopTestDataId = this.loopForm.get("testDataId").value;
    this.testStep.forLoopStartIndex = this.loopForm.get("startIndex").value;
    this.testStep.forLoopEndIndex = this.loopForm.get("endIndex").value;
    this.saving = true;
    this.testStepService.update(this.testStep).subscribe((step) => {
      step.testData = this.testStep.testData;
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

  cancel() {
    delete this.testStep.testData;
    this.testStep.testData = this.testDataList.content.find(data => data.id == this.oldData.testDataId);
    this.setStartValue(this.testStep.testData, this.oldData.startIndex, this.oldData.endIndex);
    this.onCancel.emit();
  }

  isNotNumber(index: any) {
    return isNaN(parseInt(index));
  }

}
