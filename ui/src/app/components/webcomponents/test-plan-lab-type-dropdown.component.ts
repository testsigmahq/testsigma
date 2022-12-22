import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {SelectTestLabComponent} from "./select-test-lab.component";
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanType} from "../../enums/execution-type.enum";
import {TestDevice} from "../../models/test-device.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {IntegrationsService} from "../../shared/services/integrations.service";
import {FormGroup} from "@angular/forms";
import {TestLabFormControls} from "../../enums/test-lab-form-controls"

@Component({
  selector: 'app-test-plan-lab-type-dropdown',
  templateUrl: './test-plan-lab-type-dropdown.component.html',
})
export class TestPlanLabTypeDropdownComponent extends SelectTestLabComponent implements OnInit {
  @Input('execution') execution: TestPlan;
  @Input('executionType') executionType: TestPlanType;
  @Input('executionEnvironments') executionEnvironments: TestDevice[];
  @Input('activeExecutionEnvIndex') activeExecutionEnvIndex: number = 0;
  @Input('isNewUI') isNewUI: boolean;
  @Input('executionEnvironment')  executionEnvironment:TestDevice;

  @Output('onVersionSelect') onVersionSelect = new EventEmitter<WorkspaceVersion>();
  @Output('onPreRequisiteSelect') onPreRequisiteSelect = new EventEmitter<TestDevice|undefined>();

  public ExecutionLabType = TestPlanLabType;
  public selectedVersion: WorkspaceVersion;
  public preRequisiteExecutionEnvironment: TestDevice;

  public testLabsMap = {
    [TestPlanLabType.TestsigmaLab]: {
      name: 'execution.lab_type.TestsigmaLab',
      iconClass: 'testsigma-lab-logo',
      value: TestPlanLabType.TestsigmaLab,
      isVisible: ()=> true
    },
    [TestPlanLabType.Hybrid]: {
      name: 'execution.lab_type.Hybrid',
      iconClass: 'testsigma-local-devices-logo',
      value: TestPlanLabType.Hybrid,
      isVisible: ()=> true
    }
  };

  constructor(public externalApplicationConfigService: IntegrationsService,
              public authGuard: AuthenticationGuard,
              private versionService: WorkspaceVersionService) {
    super(externalApplicationConfigService, authGuard);
    super.ngOnInit();
    console.log(this.execution);
  }

  ngOnInit(): void {
    this.versionService.show(this.selectTestLabForm.controls['workspaceVersionId'].value).subscribe(res => {
      this.selectedVersion = res;
      this.version = res;
    })

    let executionEnv = this.executionEnvironments.find((item, idx)=> idx == this.activeExecutionEnvIndex);
    this.preRequisiteExecutionEnvironment = this.executionEnvironments.find((item, idx)=>((item.id == executionEnv?.prerequisiteTestDevicesId && item.id) || (executionEnv?.prerequisiteTestDevicesIdIndex == idx)));
    if(this.executionEnvironment){
      this.selectTestLabForm.controls[TestLabFormControls.TESTPLAN_LABTYPE].setValue(this.executionEnvironment.testPlanLabType)
    }
  }

  ngOnChanges(simpleChanges: SimpleChanges) {
    if(simpleChanges?.version) {
      this.selectedVersion = simpleChanges?.version?.currentValue;
      this.version = simpleChanges?.version?.currentValue;
    }
    if(simpleChanges?.executionType){
      // @ts-ignore
      this.executionType = simpleChanges?.executionType.currentValue;
    }
  }

  setValue(testsigmaLab: TestPlanLabType) {
    this.selectTestLabForm.controls['testPlanLabType'].setValue(testsigmaLab);
    super.setTargetMachineAsMandatory(false);
  }

  get testPlanLabType(){
    return this.selectTestLabForm.controls['testPlanLabType'].value;
  }

  get isDistributed(){
    return this.execution?.testPlanType == TestPlanType.DISTRIBUTED;
  }

  setVersion(version: WorkspaceVersion) {
    this.selectTestLabForm.controls['workspaceVersionId'].setValue(version.id);
    this.setValue(TestPlanLabType.TestsigmaLab);
    this.selectedVersion = version;
    this.onVersionSelect.emit(this.selectedVersion);
  }

  selectEnvironmentPreRequisite(executionEnvironment?: TestDevice | undefined) {
    this.preRequisiteExecutionEnvironment = executionEnvironment;
    this.onPreRequisiteSelect.emit(executionEnvironment);
  }

  get testLabsList() {
    return Object['values'](this.testLabsMap).filter((item)=> item.isVisible());
  }

  get prerequisiteList() {
    let activeEnvID = this.selectTestLabForm.value.id;
    let executionEnv = this.executionEnvironments.find((item, idx)=> idx == this.activeExecutionEnvIndex);
    return this.executionEnvironments.filter((item, idx)=> {
      let isItemPrecessor = idx < this.activeExecutionEnvIndex;
      let isItemNotRelated = activeEnvID? (item.id != activeEnvID && item.prerequisiteTestDevicesId != activeEnvID) : ( idx != this.activeExecutionEnvIndex && item.prerequisiteTestDevicesIdIndex != this.activeExecutionEnvIndex);
      let isitemNotSelected = activeEnvID? (executionEnv?.prerequisiteTestDevicesId != item.id) : ( this.selectTestLabForm.value.prerequisiteTestDevicesIdIndex != idx );

      return isItemPrecessor && isItemNotRelated && isitemNotSelected;
    });
  }
}
