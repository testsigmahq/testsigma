import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {MatHorizontalStepper} from "@angular/material/stepper";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatSelect} from "@angular/material/select";
import {TestSuite} from "../../../models/test-suite.model";
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {TestDevice} from "../../../models/test-device.model";
import {TestPlan} from "../../../models/test-plan.model";
import {TestSuiteService} from "../../../services/test-suite.service";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import { WorkspaceVersionService } from 'app/shared/services/workspace-version.service';
import { TestDeviceService } from 'app/services/test-device.service';
import {delay} from "rxjs/operators";
import {TestPlanType} from "../../../enums/execution-type.enum";
import {TestPlanAddSuiteFormComponent} from "../../webcomponents/test-plan-add-suite-form.component";
import {TestPlanMachineSelectionFormComponent} from "./test-plan-machine-selection-form.component";
import {CdkDragDrop, moveItemInArray} from "@angular/cdk/drag-drop";
import {TestStep} from "../../../models/test-step.model";
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";
import {ApplicationPathType} from "../../../enums/application-path-type.enum";

@Component({
  selector: 'app-test-plan-suite-machine-selection',
  templateUrl: './test-plan-suite-machine-selection.component.html',
  styleUrls: ['./test-plan-suite-machine-selection.component.scss']
})
export class TestPlanSuiteMachineSelectionComponent extends BaseComponent implements OnInit {
  @Input('formGroup') testPlanForm: FormGroup;
  @Input('formSubmitted') formSubmitted: boolean;
  @Input('version') version: WorkspaceVersion;
  @Input('execution') execution: TestPlan;
  @Input('stepper') stepper: MatHorizontalStepper;
  @Input('tabPosition') tabPosition: Number;

  @Output('updateHeaderBtns') updateHeaderBtns = new EventEmitter<{tabPosition: Number, buttons: any[]}>();
  @Output('hasTestSuitesWithoutMachine') hasTestSuitesWithoutMachine = new EventEmitter<any>();

  @ViewChild('selectToggle') selectToggle: MatSelect;
  @ViewChild('headerLHS') headerLHS: HTMLElement;

  public executionEnvironments: TestDevice[] = [];

  public testSuiteList = [];
  public selectedTestSuites = [];
  public testSuiteIdMap: {[key: string]: TestSuite}= {};

  public searchQuery = '';
  public filter : any = 'All';
  public versionFilter: WorkspaceVersion | null = null;

  public applicationVersionsMap = {};

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    private formBuilder: FormBuilder,
    private matDialog: MatDialog,
    public applicationVersionService: WorkspaceVersionService,
    private testSuiteService: TestSuiteService,
    private executionEnvironmentService: TestDeviceService) {
    super(authGuard, notificationsService, translate);
  }

  ngOnInit(): void {
    this.invokeBtnState();
    this.testPlanForm.valueChanges.pipe(delay(100)).subscribe(()=> {
      this.invokeBtnState();
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    let removeInvalidEnvs = [];
    if(!changes.execution) return;

    if(!this.testPlanForm?.value?.testDevices?.length || !this.executionEnvironments.length) {
      let envs = changes.execution?.currentValue?.testDevices || this.testPlanForm?.value?.testDevices || [];
      let suites = envs?.map(item => item.testSuites) || [];

      this.executionEnvironments = envs;

      this.testPlanForm.controls['testPlanType'].setValue(TestPlanType.CROSS_BROWSER);

      if(!this.testPlanForm?.value?.environments?.length) {
        envs.forEach((environment) => {
          (<FormArray>this.testPlanForm.controls['testDevices']).push(this.createEnvironmentFormGroup(environment))
        });
      }

      this.executionEnvironments.forEach((env, idx) => {
        if(!env.suiteIds?.length && !env.testSuites?.length) {
          removeInvalidEnvs.push(env);
          return;
        }
        if (!env.suiteIds && env.testSuites) {
          this.setTestMachineSettings({
            testDevice: env,
            key: 'suiteIds',
            value: env.testSuites.map(suite => suite.id)
          });
        }

        if(!env.testSuites) this.setTestSuite(env);
      })

      removeInvalidEnvs.forEach(env=> this.removeExecutionEnvironment(env));
      this.makeTestSuiteMap(suites);
      this.testSuiteList = this.computePreRequisite(this.testSuiteList);
    }
  }

  makeTestSuiteMap(suites: TestSuite[]) {
    this.testSuiteList = [];

    [].concat.apply([], suites).forEach(testsuite => {
      if (!this.testSuiteList.filter(i=>i).find(item => item.id == testsuite.id)) (testsuite && this.testSuiteList.push(testsuite));
    })

    this.constructTestSuiteIdMap();
  }

  addSuites() {
    let selectedTestSuites = this.filter == 'All'? (this.versionFilter? this.testSuiteList.filter(item=> item.workspaceVersionId == this.versionFilter.id) : this.testSuiteList) : this.filter?.testSuites;
    let testDevice = new TestDevice();
    testDevice.testSuites =selectedTestSuites;
    this.matDialog.open(TestPlanAddSuiteFormComponent, {
      width: '80vw',
      height: '85vh',
      data: {testDevice:testDevice, version: this.versionFilter || this.selectedVersion, execution: this.execution, returnTestSuites: true, isE2E: (this.filter == 'All' && !this.isRest && !this.versionFilter) },
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    }).afterClosed().subscribe(res => {
      if(res) {
        if(this.filter == 'All') {
          //this.testSuiteList = testDevice.testSuites;
          this.testSuiteList = this.versionFilter? [...this.testSuiteList.filter(item=> item.workspaceVersionId != this.versionFilter.id), ...res] : res;
          //this.selectedTestSuites = this.hasMixedAppVersion? [] : res;
          this.selectedTestSuites = this.testSuiteList;
          this.constructTestSuiteIdMap();
        } else {
          this.mapTestSuitesToMachine(this.filter, res);
          res.forEach(item=> {
            if(this.testSuiteIdMap[item.id] === undefined && item) this.testSuiteList.push(item);
          });
        }
        this.constructTestSuiteIdMap();
        this.executionEnvironmentService.formValueChanges.next();
      }
      this.constructTestSuiteIdMap();
      this.invokeBtnState();
    });
  }

  mapTestSuitesToMachine(testDevice: TestDevice, testSuites: TestSuite[], isOverwrite = true) {
    if(!isOverwrite) {
      let currentSuites = testDevice.testSuites || this.testSuiteList.filter(suite=> testDevice.suiteIds.includes(suite.id));
      let currentSuiteIds = currentSuites.map(i=>i.id);
      testSuites = [...currentSuites, ...testSuites.filter(item=> !currentSuiteIds.includes(item.id))];
    }

    testSuites = this.getLinkedTestSuites(testSuites);

    this.selectedTestSuites = [];

    this.setTestMachineSettings({ testDevice, key: 'suiteIds', value: testSuites.map(item => item.id) });
    this.setTestMachineSettings({ testDevice, key: 'testSuites', value: testSuites });

    this.constructTestSuiteIdMap();
    this.executionEnvironmentService.formValueChanges.next();
  }

  emptyTestSuiteSelection() {
    this.selectedTestSuites = [];
  }

  handleCreateMachine() {
    let versionFilter = (item)=> (!this.versionFilter || item.workspaceVersionId == this.versionFilter.id);
    let allFilter = (item)=> (this.filter == 'All' || item.workspaceVersionId == this.filter.workspaceVersionId);
    let testSuites = this.selectedTestSuites.filter((item: TestSuite)=> ( versionFilter(item) && allFilter(item)) );
    this.openSelectTestMachineForm(null, testSuites);
  }

  openSelectTestMachineForm(environment?, testSuites: TestSuite[] = []) {
    this.matDialog.open(TestPlanMachineSelectionFormComponent, {
      width: '750px',
      height: '100vh',
      data: {
        execution: this.testPlanForm.value,
        executionType: this.testPlanForm?.value?.executionType,
        executionEnvironments: this.executionEnvironments,
        executionEnvironment: environment,
        version: this.applicationVersionsMap[testSuites[0].workspaceVersionId],
        testSuites: this.getLinkedTestSuites(testSuites)
      },
      position: {top: '0px', right: '0px'},
      panelClass: ['mat-dialog', 'rds-none']
    }).afterClosed().subscribe((res) => {

      if(!res) return;

      let {formGroup,isEdit} = res;
      let environments = <FormArray>this.testPlanForm.controls['testDevices'];

      if (formGroup && !isEdit) { // save
        environments.push(formGroup);
        this.executionEnvironments.push( new TestDevice().deserialize(formGroup.getRawValue()) );
        this.emptyTestSuiteSelection();
        this.mapTestsuite(this.executionEnvironments.slice(-1)[0]);
      } else if(formGroup && isEdit) { // update
        let idx = this.executionEnvironments.indexOf(environment);

        environments.setControl(idx, formGroup);
        this.executionEnvironments[idx] = new TestDevice().deserialize(formGroup.getRawValue());
        this.mapTestsuite(this.executionEnvironments[idx]);
      }
      this.executionEnvironmentService.formValueChanges.next();
    });
  }

  selectAllTestSuites(event: any) {
    if(!event.checked) this.emptyTestSuiteSelection();
    else this.selectedTestSuites = [...this.testSuiteList];
  }

  removeTestSuite({testsuite, executionEnvironments}) {
    if(this.filter == 'All') {
      this.testSuiteList = this.testSuiteList.filter(suite => suite.id != testsuite.id);
      this.selectedTestSuites = this.selectedTestSuites.filter(suite => suite.id != testsuite.id);
      this.testSuiteList = this.computePreRequisite(this.testSuiteList);

      // unlink the testsuite from present environments
      executionEnvironments.forEach(testDevice=> {
        this.unlinkTestsuite({testsuite, testDevice});
      });
    } else {
      this.unlinkTestsuite({testsuite, testDevice: this.filter});
      if(testsuite.preRequisite && !this.filter.testSuites.find(item=> (item.preRequisite == testsuite.preRequisite && item.id != testsuite.id))) {
        this.removeTestSuite({testsuite: this.testSuiteIdMap[testsuite.preRequisite], executionEnvironments});
      }
    }
    this.invokeBtnState();
  }

  unlinkTestsuite({ testsuite, testDevice }) {
    if(testDevice) {
      let idx = this.executionEnvironments.findIndex(item => item == testDevice);
      let environments = <FormArray>this.testPlanForm.controls['testDevices'];
      let suiteIds = environments?.controls[idx]?.value.suiteIds.filter(id => id != testsuite.id) || [];

      if(!suiteIds.length) { // If executionEnvironment has no test suite, then remove it
        this.removeExecutionEnvironment(this.executionEnvironments[idx]);
        return;
      }

      this.setTestMachineSettings({ testDevice, key: 'suiteIds', value: suiteIds});
      this.setTestMachineSettings({ testDevice, key: 'testSuites', value: suiteIds.map(id=> this.testSuiteIdMap[id] )});
    } else {
      this.executionEnvironments.forEach(env => this.unlinkTestsuite({testsuite, testDevice: env }));
    }
    this.invokeBtnState();
    this.executionEnvironmentService.formValueChanges.next();
  }

  removeExecutionEnvironment(executionEnvironment: TestDevice) {
    let idx = this.executionEnvironments.findIndex(item => item == executionEnvironment);
    let environmentsForm = <FormArray>this.testPlanForm.controls['testDevices'];

    if(executionEnvironment == this.filter) this.filter = 'All';

    environmentsForm.controls.splice(idx, 1);
    this.executionEnvironments.splice(idx, 1);

    this.invokeBtnState();
  }

  editExecutionEnvironment(executionEnvironment: TestDevice) {
    this.openSelectTestMachineForm(executionEnvironment, executionEnvironment?.testSuites);
  }

  handleTestSuiteItemCheckbox(event: { type: 'selected' | 'unselected', testsuite: TestSuite}) {
    let isSelected = this.selectedTestSuites.includes(event.testsuite);
    if(event.type == 'selected' && !isSelected) this.selectedTestSuites.push(event.testsuite);
    else if(event.type == 'unselected' && isSelected) this.selectedTestSuites = this.selectedTestSuites.filter(suite => suite.id != event.testsuite.id);
  }

  handleSuiteLevelExecutionMethodChange({ testSuite, isTestCaseParallel }) {
    let value = isTestCaseParallel? this.filter.runTestCasesInSequenceSuiteIds.filter(id=> id != testSuite.id) : [...this.filter.runTestCasesInSequenceSuiteIds, testSuite.id];

    if(this.filter.testSuites.length == 1) {
      this.setTestMachineSettings({ testDevice: this.filter, key: 'createSessionAtCaseLevel', value: isTestCaseParallel });
      this.setTestMachineSettings({ testDevice: this.filter, key: 'runTestCasesInParallel', value: isTestCaseParallel });
    }

    this.setTestMachineSettings({ testDevice: this.filter, key: 'runTestCasesInSequenceSuiteIds', value });
  }

  setTestMachineSettings({ testDevice, key, value }) {
    let idx = this.executionEnvironments.indexOf(testDevice);
    let environmentsForm = <FormArray>this.testPlanForm.controls['testDevices'];

    environmentsForm.controls[idx].get(key)?.patchValue(value);
    if(key == 'runTestCasesInParallel' && !value) {
      environmentsForm.controls[idx].get('runTestSuitesInParallel')?.patchValue(value);
    }
    this.executionEnvironments[idx][key] = value;
  }

  invokeBtnState() {
    this.constructTestSuiteIdMap();
    this.updateHeaderBtns.emit({
      tabPosition: this.tabPosition,
      buttons: [
        {
          className: 'theme-btn-clear-default',
          content: this.translate.instant('pagination.previous'),
          clickHandler: ()=> this.stepper.previous()
        },
        {
          className: 'theme-btn-primary ml-15',
          content: this.translate.instant('pagination.next'),
          isDisabled: false,//this.executionEnvironments.length == 0 || this.isNextDisabled,
          clickHandler: ()=> this.handleNext()
        }
      ]
    });

    this.hasTestSuitesWithoutMachine.emit(this.isNextDisabled);
    this.executionEnvironmentService.formValueChanges.next();
  }

  handleNext() {
    this.emptyTestSuiteSelection();
    this.versionFilter = null;
    this.filter = 'All';
    this.stepper.next();
  }

  testSuiteDrop(event: CdkDragDrop<TestStep[]>) {
    if(!this.filter?.suiteIds && !this.filter?.testSuites?.length) return;

    if(this.filter?.suiteIds?.length) {
      let suiteIds = this.filteredTestsuites.map(item => item.id);
      let testSuite = this.testSuiteIdMap[suiteIds[event.previousIndex]];

      moveItemInArray(suiteIds, event.previousIndex, event.currentIndex);

      if(testSuite?.preRequisite) {
        suiteIds = this.alignTestSuites(suiteIds, testSuite, event.currentIndex);
      }

      this.setTestMachineSettings({testDevice: this.filter, key: 'suiteIds', value: suiteIds});
      this.setTestMachineSettings({testDevice: this.filter, key: 'testSuites', value: suiteIds.map(id=> this.testSuiteIdMap[id])});
    }
  }

  searchTestsuite(event) {
    this.searchQuery = event?.trim() || '';
  }

  setFilter(event) {
    if(event) this.selectToggle?.close();
    if(!event.value) return;

    this.setFilterValue(event.value);
  }

  createEnvironmentFormGroup(environment?: TestDevice) {
    let suiteIds = environment?.testSuites?.map(v => v.id);
    let createSessionAtCaseLevel = environment?.createSessionAtCaseLevel || false;

    let environmentFormGroup =  new FormGroup({
      id: new FormControl(environment?.id, []),
      testPlanLabType: new FormControl(environment?.testPlanLabType || TestPlanLabType.TestsigmaLab, []),
      workspaceVersionId: new FormControl(environment?.workspaceVersionId || this.version.id, []),
      prerequisiteTestDevicesId: new FormControl(environment?.prerequisiteTestDevicesId, []),
      prerequisiteTestDevicesIdIndex: new FormControl(environment?.prerequisiteTestDevicesIdIndex, []),
      //targetMachine: new FormControl(environment?.targetMachine, []),
      deviceId: new FormControl(environment?.deviceId, []),
      suiteIds: this.formBuilder.control(suiteIds, Validators.required),

      title: new FormControl(environment?.title, []),
      createSessionAtCaseLevel: new FormControl(createSessionAtCaseLevel, []),
      platformOsVersionId : new FormControl(environment?.platformOsVersionId, []),
      platformBrowserVersionId : new FormControl(environment?.platformBrowserVersionId, []),
      platformScreenResolutionId : new FormControl(environment?.platformScreenResolutionId, []),
      platformDeviceId : new FormControl(environment?.platformDeviceId, []),
      platform: new FormControl(environment?.platform, []),
      osVersion: new FormControl(environment?.osVersion, []),
      browser: new FormControl(environment?.browser, []),
      browserVersion: new FormControl(environment?.browserVersion, []),
      resolution: new FormControl(environment?.resolution, []),
      deviceName: new FormControl(environment?.deviceName, []),
      capabilities : new FormControl(environment?.capabilities, [])
    });

    if(this.selectedVersion.workspace.isMobileNative || environment?.appPathType) {
      return this.addControls(environmentFormGroup, environment);
    }
    return environmentFormGroup;
  }

  addControls(environmentFormGroup: FormGroup, environment?: TestDevice) {
    environmentFormGroup.addControl('deviceId', new FormControl(environment?.deviceId, []));
    environmentFormGroup.addControl('appPathType', new FormControl(environment?.appPathType || ApplicationPathType.UPLOADS, []));
    environmentFormGroup.addControl('appUploadId', new FormControl(environment?.appUploadId, []));
    environmentFormGroup.addControl('appUploadVersionId', new FormControl(environment?.appUploadVersionId, []));
    environmentFormGroup.addControl('appUrl', new FormControl(environment?.appUrl, []));
    if (this.selectedVersion?.application?.isAndroidNative) {
      environmentFormGroup.addControl('appPackage', new FormControl(environment?.appPackage, []));
      environmentFormGroup.addControl('appActivity', new FormControl(environment?.appActivity, []));
    }
    else {
      environmentFormGroup.addControl('appBundleId', new FormControl(environment?.appBundleId, []));
    }
    return environmentFormGroup;
  }

  computePreRequisite(testSuites) {
    let operation = '';
    let computed: any= {preRequisite: [], nonPreRequisite: []};

    testSuites.forEach(suite=> {
      suite.parentSuite = testSuites.find(item=> item.preRequisite == suite.id);
      operation = (suite.parentSuite && computed.preRequisite.find(item=> item?.preRequisite == suite.id ))? 'unshift' : 'push';
      computed[ suite.parentSuite? 'preRequisite' : 'nonPreRequisite' ][operation](Object.assign({}, suite));
    });

    return [...computed.preRequisite, ...computed.nonPreRequisite];
  }

  constructTestSuiteIdMap() {
    this.testSuiteIdMap = {};
    this.testSuiteList.forEach(suite=> this.testSuiteIdMap[suite.id] = suite);
  }

  mapTestsuite(environment: TestDevice) {
    if(!environment?.testSuites) {
      environment.testSuites = environment.suiteIds.map(id=> this.testSuiteIdMap[id]);
    }
  }

  getLinkedTestSuites(testSuites: TestSuite[]) {
    let map = testSuites.reduce((prev, cur)=> Object.assign(prev, {[cur.id]: cur}), {});
    let list = testSuites;
    let handler = (testsuite)=> {
      if(testsuite.preRequisite && !map[testsuite.preRequisite]) {
        let prereq = this.testSuiteIdMap[testsuite.preRequisite];
        list.unshift(prereq);
        map[prereq.id] = prereq;
        handler(prereq);
      }
    }

    list.forEach(suite=> handler(suite));

    return list;//this.computePreRequisite(list);
  }

  alignTestSuites(suiteIds, suite, idx) {
    let suites = suiteIds.map(id=> this.testSuiteIdMap[id]);
    let handler = (suite, prereq, suiteIdx)=> {
      let prereqIdx = suites.findIndex(item => item.id == suite.preRequisite);
      if(prereqIdx > suiteIdx) moveItemInArray(suites, prereqIdx, suiteIdx);
      if(prereqIdx < suiteIdx) moveItemInArray(suites, prereqIdx, suiteIdx - 1);
      if(prereq.preRequisite) handler(prereq, this.testSuiteIdMap[prereq.preRequisite], suiteIdx);
    }

    handler(suite, this.testSuiteIdMap[suite.preRequisite], idx);
    return suites.map(item => item.id)
  }

  fetchApplicationVersion(id: number) {
    if(this.applicationVersionsMap[id] !== null) {
      this.applicationVersionsMap[id] = null;
      this.applicationVersionService.show(id).subscribe(data => this.applicationVersionsMap[id] = data);
    }
    return this.applicationVersionsMap[id];
  }

  setTestSuite(environment : TestDevice) {
    let suiteIds = environment.suiteIds;
    if(!environment.testSuites && suiteIds.length) {
      this.testSuiteService.findAll(`id@${suiteIds.join('#')}`).subscribe(res => {
        environment.testSuites = res.content;
        this.makeTestSuiteMap([...this.testSuiteList, res.content]);
      })
    }
  }

  setFilterValue(value) {
    this.filter = value;
    this.selectedTestSuites = [];
    this.setTestsuiteVersionFilterValue(null);
  }

  setTestsuiteVersionFilterValue(value) {
    this.versionFilter = value;
    this.selectedTestSuites = [];
  }


  get selectedVersion() {
    return (this.filter == 'All'? this.versionFilter :this.applicationVersionsMap[this.filter?.workspaceVersionId]) || this.applicationVersionsMap[this.currentSelectedVersion] || this.version;
  }

  get filteredTestsuites() {
    let list = this.filter == 'All'? this.testSuiteList : this.filter?.suiteIds?.map(id=> this.testSuiteIdMap[id]);
    return list.filter(item => item && item?.name.match(new RegExp(this.searchQuery, 'i')) && (this.versionFilter == null || item.workspaceVersionId == this.versionFilter.id));
  }

  get isNextDisabled() {
    let map = [];
    this.executionEnvironments.forEach(env => map = [...map, ...env?.suiteIds? env?.suiteIds:[] ]);

    for(let i=0;i< this.testSuiteList.length;i++) {
      if(!map.includes( this.testSuiteList[i].id )) return true;
    }

    return false;
  }

  get hasMixedAppVersion() {
    return [...new Set(this.testSuiteList?.map(item=> item?.workspaceVersionId).filter(i=>i))].length > 1;
  }

  get currentSelectedVersion() {
    return this.selectedTestSuites?.[0]?.workspaceVersionId;
  }

  get selectedTestSuiteIds() {
    return this.selectedTestSuites.map(item=> item.id);
  }

  get isRest() {
    return this.version?.workspace?.isRest;
  }
}
