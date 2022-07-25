import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TestDevice} from "../../../models/test-device.model";
import {TestDeviceSettings} from "../../../models/test-device-settings.model";
import {TestSuite} from "../../../models/test-suite.model";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";

@Component({
  selector: 'app-test-plan-env-tags',
  template: `
    <div module="test-env-tags" class="d-inline-flex">
    <span class="align-items-center d-inline-flex env-tag rb-light" *ngIf="filteredEnvironments[0]">
      <!--tooltipIfTruncated-->
      <span class="tag-content text-truncate d-inline-block" [matTooltip]="filteredEnvironments[0].title">
        {{ filteredEnvironments[0].title }}
      </span>
      <i
        class="fa-close-alt fz-10 pointer ml-10"
        [ngClass]="{'disabled': isRemoveDisabled[0]}"
        [matTooltip]="(isRemoveDisabled[0]? 'test_plan.prereq.operation.blocked' : 'test_plan.remove.machine') | translate"
        (click)="!isRemoveDisabled[0] && unlinkTestsuite(filteredEnvironments[0])"></i>
    </span>
      <span class="align-items-center d-inline-flex env-tag fz-10 ml-5 pointer" #trigger="cdkOverlayOrigin" cdkOverlayOrigin (click)="openTagsDialog()" *ngIf="moreEnvs.length">
    </span>

      <ng-template
        #envTagsDialog="cdkConnectedOverlay"
        cdkConnectedOverlay
        [cdkConnectedOverlayOrigin]="trigger"
        [cdkConnectedOverlayOpen]="isOpen"
        cdkConnectedOverlayPanelClass="filter-dropdown">
        <div class="plan-tags-popup d-flex flex-wrap">
        <span class="align-items-center d-inline-flex env-tag rb-light" *ngFor="let env of moreEnvs; let i=index;">
          <!--tooltipIfTruncated-->s
          <span class="tag-content text-truncate d-inline-block" [matTooltip]="env.title">{{ env.title }}</span>
          <i
            class="fa-close-alt fz-10 pointer ml-10"
            [ngClass]="{'disabled': isRemoveDisabled[i+1]}"
            [matTooltip]="(isRemoveDisabled[i+1]? 'test_plan.prereq.operation.blocked' : 'test_plan.remove.machine') | translate"
            (click)="!isRemoveDisabled[i+1] && unlinkTestsuite(env)"></i>
        </span>
        </div>
      </ng-template>
    </div>`,
  styleUrls: ['./test-plan-env-tags.component.scss']
})
export class TestPlanEnvTagsComponent implements OnInit {
  public environmentSettings: TestDeviceSettings;

  @Input('executionEnvironments') executionEnvironments: TestDevice[];
  @Input('testsuite') testsuite: TestSuite;  isOpen = false;
  @Input('filteredEnvironments') filteredEnvironments: TestDevice[];

  @Output('onUnlinkTestsuite') onUnlinkTestsuite: EventEmitter<{testsuite: TestSuite, executionEnvironment: TestDevice}> = new EventEmitter<{testsuite: TestSuite; executionEnvironment: TestDevice}>()

  @ViewChild('envTagsDialog') envTagsDialog: CdkConnectedOverlay;
  @ViewChild('trigger') trigger;

  constructor() {}

  ngOnInit() {}

  ngOnChanges(){}

  openTagsDialog(){
    this.isOpen = true;
    setTimeout(() => {
      this.envTagsDialog.overlayRef._outsidePointerEvents.subscribe(res => {
        this.envTagsDialog.overlayRef.detach();
        this.isOpen = false;
      });
    }, 200);
  }

  unlinkTestsuite(executionEnvironment) {
    let handler = () => this.onUnlinkTestsuite.emit({
      testsuite: this.testsuite,
      executionEnvironment: executionEnvironment
    });

    if(this.moreEnvs.length == 1) {
      document.body.click();
      window.setTimeout(()=> handler(), 300);
    } else handler();
  }

  get isRemoveDisabled() {
    let map = {};
    this.filteredEnvironments.forEach((environment, idx)=> {
      map[idx] = !!this.testsuite.parentSuite && environment.suiteIds.includes(this.testsuite.parentSuite.id);
    })
    return map;
  }

  get moreEnvs() {
    return this.filteredEnvironments.slice(1);
  }
}
