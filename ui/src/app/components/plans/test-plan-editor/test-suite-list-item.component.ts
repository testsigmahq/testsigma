import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";
import {TestSuite} from "../../../models/test-suite.model";
import {TestDevice} from "../../../models/test-device.model";
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";

@Component({
  selector: 'app-test-suite-list-item',
  template: `

    <!-- Skeleton loader shown while application version detail being fetched -->
    <div *ngIf="!applicationVersion"
         [attr.module]="'data-testsuite-list-item'" class="d-flex w-100 align-items-center loader-section m-0">
      <div class="text-line-loader w-10 border-rds-10 mb-0"></div>
      <div class="text-line-loader w-60 border-rds-10 mb-0 ml-20"></div>
      <div class="text-line-loader w-20 border-rds-10 mb-0 ml-50"></div>
    </div>

    <div
      *ngIf="applicationVersion"
      [attr.module]="'data-testsuite-list-item'" class="d-flex w-100 align-items-center"
      [ngClass]="{'item-selected': isSelected}"

      cdkDrag
      cdkDragBoundary=".cdk-drop-list.drag-list"
      cdkDragLockAxis="y"
      [cdkDragData]="testsuite"
      [cdkDragDisabled]="isDragDisabled"
    >

      <!-- Test suite information -->
      <div class="flex-grow-1 d-flex align-items-center">
        <i
          class="fa-drag-n-drop mr-10 drag-icon"
          [ngClass]="{'drag-disabled': isDragDisabled}"
          [matTooltip]="(isDragDisabled? (isFilteredApplied? 'test_plan.prereq.operation.blocked' : (filteredEnvironments.length? 'test_plan.filter.reorder.testsuite' : 'test_plan.filter.reorder.testsuite.no_machine')) : 'test_plan.drag.reorder.testsuite') | translate"></i>
        <mat-checkbox
          class="mat-checkbox"
          [disabled]="isDifferentAppVersion"
          [ngClass]="{'disabled': isDifferentAppVersion}"
          [matTooltip]="(isDifferentAppVersion? 'test_plan.suite.disabled.prev_diff' : '') | translate"
          (change)="handleCheckbox()"
          [checked]="isSelected"></mat-checkbox>
        <i [class]="appIcon + ' ml-15 text-secondary'" [matTooltip]="appTitle"></i>
        <i *ngIf="isPreReq" class="fa-prerequisite ml-15 text-strongBlue" [matTooltip]="'test_suites.details.label.prerequisite' | translate"></i>
        <!--tooltipIfTruncated-->
        <span class="suite-name text-truncate ml-15" [matTooltip]="testsuite.name">{{ testsuite.name }}</span>
      </div>

      <!-- Test environments -->
      <app-test-plan-env-tags
        *ngIf="!isFilteredApplied"
        (onUnlinkTestsuite)="handleUnlinkTestsuite($event)"
        [executionEnvironments]="executionEnvironments"
        [filteredEnvironments]="filteredEnvironments"
        [testsuite]="testsuite"></app-test-plan-env-tags>

      <!-- Action Icons -->
      <div class="ml-15 action-icons">
        <i
          class="fa-trash-thin pointer hover-only"
          [ngClass]="{'disabled': isPreReq}"
          (click)="!isPreReq && removeSuite()"
          [matTooltip]="(isPreReq? 'test_plan.prereq.operation.blocked':'btn.common.delete') | translate"></i>
        <app-create-machine-button
          [isDisabled]="false"
          [tooltip]="''"
          [executionEnvironments]="executionEnvironments"
          [testSuiteList]="testSuiteList"
          [selectedTestSuites]="[testsuite]"
          [applicationVersionsMap]="applicationVersionsMap"
          [isIconOnly]="true"
          (onCreateMachine)="addMachine()"
          (onMapMachine)="onMapMachine.emit([$event, [testsuite], false])"
        ></app-create-machine-button>
      </div>
    </div>`,
  styleUrls: ['./test-suite-list-item.component.scss']
})
export class TestSuiteListItemComponent{
  @Input('executionEnvironments') executionEnvironments: TestDevice[];
  @Input('testsuite') testsuite: TestSuite;
  @Input('isSelected') isSelected: boolean;
  @Input('disableDrag') disableDrag: boolean;
  @Input('isFilteredApplied') isFilteredApplied: boolean;
  @Input('applicationVersion') applicationVersion: WorkspaceVersion | undefined | null;
  @Input('currentSelectedVersion') currentSelectedVersion: number | undefined;
  @Input('selectedExecutionEnvironment') selectedExecutionEnvironment: TestDevice | null;

  @Output('onDelete') onDelete: EventEmitter<{ testsuite: TestSuite, executionEnvironments: TestDevice[]}> = new EventEmitter<{ testsuite: TestSuite, executionEnvironments: TestDevice[]}>();
  @Output('onAddMachine') onAddMachine: EventEmitter<TestSuite> = new EventEmitter<TestSuite>();
  @Output('onUnlinkTestsuite') onUnlinkTestsuite: EventEmitter<{ testsuite: TestSuite, executionEnvironment: TestDevice | undefined }> = new EventEmitter<{ testsuite: TestSuite, executionEnvironment: TestDevice | undefined }>();
  @Output('onSelectChange') onSelectChange: EventEmitter<{ type: 'selected' | 'unselected', testsuite: TestSuite}> = new EventEmitter<{ type: 'selected' | 'unselected', testsuite: TestSuite}>();
  @Output('onSuiteLevelExecutionMethodChange') onSuiteLevelExecutionMethodChange: EventEmitter<{testSuite: TestSuite, isTestCaseParallel:Boolean}> = new EventEmitter<{ testSuite: TestSuite, isTestCaseParallel:Boolean }>();

  @ViewChild('trigger') trigger;
  @Input('testSuiteList') testSuiteList: TestSuite[];
  @Input('applicationVersionsMap') applicationVersionsMap: Object;
  @Output('onMapMachine') onMapMachine: EventEmitter<any> = new EventEmitter();

  constructor() {}

  handleCheckbox() {
    if(!this.isDifferentAppVersion) this.onSelectChange.emit({ type: this.isSelected? 'unselected' : 'selected', testsuite: this.testsuite });
  }

  removeSuite() {
    this.onDelete.emit({ testsuite: this.testsuite, executionEnvironments: this.testSuitePresentExecutionEnvironments });
  }

  addMachine() {
    this.onAddMachine.emit(this.testsuite);
  }

  handleUnlinkTestsuite(event: { testsuite: TestSuite, executionEnvironment: TestDevice | undefined }) {
    this.onUnlinkTestsuite.emit(event);
  }


  setTestCaseExecMethod(event) {
    this.onSuiteLevelExecutionMethodChange.emit({
      testSuite: this.testsuite,
      isTestCaseParallel: event.checked
    });
  }

  get testSuitePresentExecutionEnvironments() {
    return this.executionEnvironments.filter(env => env.suiteIds?.includes(this.testsuite?.id));
  }

  get isPreReq() {
    return !!this.testsuite.parentSuite && ( !this.selectedExecutionEnvironment || this.selectedExecutionEnvironment.suiteIds.includes(this.testsuite.parentSuite.id) );
  }

  get isDragDisabled() {
    return this.isPreReq || this.disableDrag;
  }

  get isMobile() {
    return this.applicationVersion?.workspace?.isMobile;
  }

  get isAndroid() {
    return this.applicationVersion?.workspace?.isAndroidNative;
  }

  get isIOS() {
    return this.applicationVersion?.workspace?.isIosNative;
  }

  get isMobileWeb() {
    return this.applicationVersion?.workspace?.isMobileWeb;
  }

  get isRest() {
    return this.applicationVersion?.workspace?.isRest;
  }

  get isWeb() {
    return this.applicationVersion?.workspace?.isWeb;
  }

  get isHybrid() {
    return this.selectedExecutionEnvironment?.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get appIcon() {
    let map = {
      'mt-4 fa-project-website': this.isWeb,
      'fa-android-solid': this.isAndroid,
      'fa-apple': this.isIOS,
      'fa-project-mobile': this.isMobileWeb,
      'fa-rest-new': this.isRest
    };

    for(let key in map) {
      if(map[key]) return key;
    }

    return '';
  }

  get appTitle() {
    if(!this.applicationVersion) return '';
    //return this.applicationVersion?.application.project.name + ' / ' + this.applicationVersion.workspace.name + ' / ' + this.applicationVersion.versionName;
    return this.applicationVersion.workspace.name + ' / ' + this.applicationVersion.versionName;
  }

  get isDifferentAppVersion() {
    return this.currentSelectedVersion !== undefined && this.currentSelectedVersion != this.applicationVersion?.id;
  }

  get filteredEnvironments() {
    return this.executionEnvironments.filter(env => env.suiteIds.includes(this.testsuite.id));
  }


}
